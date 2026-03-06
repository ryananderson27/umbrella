package com.example.umbrella

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.example.umbrella.data.AccelerometerForegroundService
import com.example.umbrella.data.UserWeatherDataStore
import com.example.umbrella.data.WeatherRepository
import com.example.umbrella.ui.theme.UmbrellaTheme
import com.example.umbrella.viewmodels.UserViewModel
import com.example.umbrella.viewmodels.UserViewModelFactory
import com.example.umbrella.viewmodels.WeatherViewModel
import com.example.umbrella.viewmodels.WeatherViewModelFactory
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class MainActivity : ComponentActivity() {

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var userViewModel: UserViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        ) {
            getLocationAndFetch()
        } else {
            Log.e("Umbrella", "Location permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AccelerometerForegroundService.start(this)
        enableEdgeToEdge()

        // Initialize ViewModels
        val repo = WeatherRepository()
        val weatherFactory = WeatherViewModelFactory(repo, application)
        weatherViewModel = ViewModelProvider(this, weatherFactory)[WeatherViewModel::class.java]
        val dataStore = UserWeatherDataStore(applicationContext)
        val userFactory = UserViewModelFactory(dataStore)
        userViewModel = ViewModelProvider(this, userFactory)[UserViewModel::class.java]

        requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        )

        setContent {
            var isDarkMode by rememberSaveable { mutableStateOf(false) }
            UmbrellaTheme(darkTheme = isDarkMode) {
                WeatherDisplay(
                    weatherViewModel = weatherViewModel,
                    userViewModel = userViewModel,
                    isDarkMode = isDarkMode,
                    onToggleTheme = { isDarkMode = !isDarkMode }
                )
            }
        }
    }

    private fun getLocationAndFetch() {
        val fusedClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            val cts = CancellationTokenSource()
            fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cts.token
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    weatherViewModel.fetchData(
                        lat = location.latitude,
                        lon = location.longitude
                    )
                } else {
                    Log.e("Umbrella", "Location is null. GPS may be off.")
                }
            }.addOnFailureListener { e ->
                Log.e("Umbrella", "Location fetch failed: ${e.message}")
            }

        } catch (e: SecurityException) {
            Log.e("Umbrella", "Permission error: ${e.message}")
        }
    }
}
