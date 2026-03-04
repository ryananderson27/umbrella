package com.example.umbrella.data

import com.example.umbrella.BuildConfig
import com.example.umbrella.models.WeatherResponse

class WeatherRepository(
    private val api: WeatherApi = WeatherRetrofitProvider.api
) {
    suspend fun fetchCurrentWeather(lat: Double?, lon: Double?): WeatherResponse {
        return api.getCurrentWeather(
            lat = lat,
            lon = lon,
            apiKey = BuildConfig.WEATHER_KEY,
            units = "imperial"
        )
    }
}