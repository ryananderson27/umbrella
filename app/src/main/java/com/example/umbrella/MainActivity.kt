package com.example.umbrella

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.umbrella.data.AccelerometerDataStore
import com.example.umbrella.data.UserWeatherDataStore
import com.example.umbrella.data.WeatherRetrofitProvider
import com.example.umbrella.ui.theme.UmbrellaTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "UmbrellaApp"
        private const val STORE_TAG = "UserStoreTest"
        private const val ACCEL_TRIGGER_X = 6f
    }

    private val weatherText = mutableStateOf("Waiting for shake...")
    private var canTriggerWeather = true

    private lateinit var userWeatherDataStore: UserWeatherDataStore

    private val requestLocationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            fetchLocationAndWeather()
        } else {
            weatherText.value = "Location permission denied"
            Log.e(TAG, "Location permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        userWeatherDataStore = UserWeatherDataStore(this)

        // Read stored values continuously so you can verify persistence in Logcat
        lifecycleScope.launch {
            userWeatherDataStore.prefsFlow.collect { prefs ->
                Log.d(STORE_TAG, "locationName = ${prefs.locationName}")
                Log.d(STORE_TAG, "latitude = ${prefs.latitude}")
                Log.d(STORE_TAG, "longitude = ${prefs.longitude}")
                Log.d(STORE_TAG, "hasUmbrella = ${prefs.hasUmbrella}")
                Log.d(STORE_TAG, "hasSnowShoes = ${prefs.hasSnowShoes}")
            }
        }

        AccelerometerForegroundService.start(this)

        lifecycleScope.launch {
            AccelerometerDataStore.latest.collect { reading ->
                reading?.let {
                    Log.d(TAG, "Accel -> x=${it.x}, y=${it.y}, z=${it.z}, t=${it.timestamp}")

                    if (it.x > ACCEL_TRIGGER_X && canTriggerWeather) {
                        canTriggerWeather = false
                        Log.d(TAG, "X above $ACCEL_TRIGGER_X, fetching weather...")
                        checkLocationPermissionAndStart()
                    }

                    if (it.x <= ACCEL_TRIGGER_X) {
                        canTriggerWeather = true
                    }
                }
            }
        }

        setContent {
            UmbrellaTheme {
                UmbrellaApp(weatherMessage = weatherText.value)
            }
        }
    }

    private fun checkLocationPermissionAndStart() {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocation || hasCoarseLocation) {
            fetchLocationAndWeather()
        } else {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocationAndWeather() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location: Location? ->
            if (location == null) {
                weatherText.value = "Could not get location"
                Log.e(TAG, "Could not get current location")
                return@addOnSuccessListener
            }

            val lat = location.latitude
            val lon = location.longitude

            Log.d(TAG, "Latitude = $lat")
            Log.d(TAG, "Longitude = $lon")

            lifecycleScope.launch {
                try {
                    // Save real GPS coordinates to DataStore
                    userWeatherDataStore.saveLocation(
                        locationName = "Current GPS Location",
                        latitude = lat,
                        longitude = lon
                    )
                    Log.d(STORE_TAG, "Saved GPS lat/lon to DataStore")

                    val weather = WeatherRetrofitProvider.api.getCurrentWeather(
                        lat = lat,
                        lon = lon,
                        apiKey = BuildConfig.WEATHER_KEY,
                        units = "imperial"
                    )

                    val firstCondition = weather.weather?.firstOrNull()

                    weatherText.value =
                        "City: ${weather.name ?: "Unknown"} | " +
                                "Temp: ${weather.main?.temp}°F | " +
                                "Condition: ${firstCondition?.description ?: "Unknown"}"

                    Log.d(TAG, weatherText.value)

                    // Update stored location name once weather returns city name
                    userWeatherDataStore.saveLocation(
                        locationName = weather.name ?: "Unknown",
                        latitude = lat,
                        longitude = lon
                    )
                    Log.d(STORE_TAG, "Updated DataStore with city name")

                } catch (e: Exception) {
                    weatherText.value = "Failed to fetch weather"
                    Log.e(TAG, "Failed to fetch weather: ${e.message}", e)
                }
            }
        }.addOnFailureListener { e ->
            weatherText.value = "Failed to get location"
            Log.e(TAG, "Failed to get location: ${e.message}", e)
        }
    }
}

@PreviewScreenSizes
@Composable
fun UmbrellaApp(weatherMessage: String = "Weather unavailable") {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Greeting(
                name = weatherMessage,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    FAVORITES("Favorites", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UmbrellaTheme {
        UmbrellaApp("City: Worcester | Temp: 42°F | Condition: light rain")
    }
}