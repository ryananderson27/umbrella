package com.example.umbrella.data

data class UserWeatherPrefs(
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val hasUmbrella: Boolean = false,
    val hasSnowShoes: Boolean = false
)