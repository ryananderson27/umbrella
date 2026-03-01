package com.example.umbrella.models

/**

Data class for wind conditions.*
@property speed Wind speed in m/s (Metric) or mph (Imperial). Defaults to null if missing from JSON.*
@version 1.0*/
data class WindData(
    val speed: Double? = null
)
