package com.example.umbrella.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.umbrella.data.AccelDS
import com.example.umbrella.data.UserWeatherDataStore
import com.example.umbrella.data.WeatherRepository
import com.example.umbrella.models.UserWeatherPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherViewModel(private val api: WeatherRepository, application: Application) : AndroidViewModel(application) {
    private val userWeatherDataStore = UserWeatherDataStore(application.applicationContext)
    val userPrefs = userWeatherDataStore.prefsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UserWeatherPrefs()
    )

    private val _weatherInfo = MutableStateFlow("Loading weather...")
    val weatherInfo: StateFlow<String> = _weatherInfo.asStateFlow()

    private val _accelerometerData = MutableStateFlow<Float?>(null)
    val accelerometerData: StateFlow<Float?> = _accelerometerData.asStateFlow()

    private val _conditionData = MutableStateFlow("Your all good")
    val conditionData: StateFlow<String> = _conditionData.asStateFlow()

    private val _lastUpdated = MutableStateFlow<String?>(null)
    val lastUpdated: StateFlow<String?> = _lastUpdated.asStateFlow()



    init {
        observeAccelerometer()
    }

    private fun observeAccelerometer() {
        viewModelScope.launch {
            AccelDS.latest.collectLatest { reading ->
                _accelerometerData.value = reading?.x
                checkConditions()
            }
        }
    }

    fun checkConditions() {
        val hasSnowBoots = userPrefs.value.hasSnowShoes
        val hasUmbrella = userPrefs.value.hasUmbrella
        val accel = accelerometerData.value ?: return
        val weather = weatherInfo.value.lowercase()

        if (accel > 3.0f || accel < -3.0f) {
            _conditionData.value =
                when {
                    "snow" in weather ->
                        if (hasSnowBoots) "Get your snow boots. It's snowy!"
                        else "Buy some snow boots. It's snowy!"
                    "rain" in weather ->
                        if (hasUmbrella) "Get your umbrella. It's rainy!"
                        else "Buy an umbrella. It's rainy!"
                    "freezing rain" in weather ->
                        if (hasUmbrella) "Get your umbrella. It's rainy!"
                        else "Buy an umbrella. It's rainy!"
                    else -> "It's a nice day!"
                }
        }
    }

    fun fetchData(lat: Double, lon: Double) {

        viewModelScope.launch {
            _weatherInfo.value = "Loading weather..."
            try {
                val current = withContext(Dispatchers.IO) {
                    api.fetchCurrentWeather(lat, lon)
                }
                userWeatherDataStore.saveLocation(current.name ?: "${"%.4f".format(lat)},${"%.4f".format(lon)}")

                _weatherInfo.value = current.weather?.firstOrNull()?.description ?: "No description"
                checkConditions()
                _lastUpdated.value = getFormattedTime()
            } catch (e: Exception) {
                e.printStackTrace()
                _weatherInfo.value =
                    "Error: ${e.localizedMessage ?: "Check your internet connection or API key."}"
            }
        }
    }


    private fun getFormattedTime(): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(Date())
    }
}