package com.example.umbrella.models

data class UserWeatherPrefs(
    val locationName: String = "",
    val hasUmbrella: Boolean = false,
    val hasSnowShoes: Boolean = false
)