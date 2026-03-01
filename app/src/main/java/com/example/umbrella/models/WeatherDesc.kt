package com.example.umbrella.models

/**

Describes the weather condition and associated iconography.*
@property description Human-readable description (e.g., "broken clouds"). Defaults to null if missing from JSON.
@property icon Unique ID used to fetch the weather icon from OpenWeather. Defaults to null if missing from JSON.*
@version 1.0*/
data class WeatherDesc(
    val description: String? = null,
    val icon: String? = null
)