package com.example.umbrella.models

/**

Data class representing core atmospheric measurements.*
@property temp Current temperature in the requested unit system. Defaults to null if missing from JSON.
@property humidity percentage. Defaults to null if missing from JSON.
@property pressure Current pressure. Defaults to null if missing from JSON.*
@version 1.0*/
data class MainData(
    val temp: Double? = null,
)
