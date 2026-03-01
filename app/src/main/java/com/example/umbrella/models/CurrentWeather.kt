package com.example.umbrella.models

data class CurrentWeather(
    val cityName: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val condition: String,
    val description: String,
    val icon: String
)