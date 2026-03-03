package com.example.umbrella.models

/**

Root response object for current weather data.*
@property name The city name. Defaults to null if missing from JSON.
@property main The core temperature data. Defaults to null if missing from JSON.
@property weather List of weather descriptions. Defaults to null if missing from JSON.
@property wind The wind speed data. Defaults to null if missing from JSON.
@property sys The sunrise/sunset data. Defaults to null if missing from JSON.
@property rain The rain data. Defaults to null if missing from JSON.*
@version 1.0*/
data class WeatherResponse(
    val name: String? = null,
    val main: MainData? = null,
    val weather: List<WeatherDesc>? = null,
    val wind: WindData? = null,
    val rain: Rain? = null
)