package com.example.umbrella.models

import com.google.gson.annotations.SerializedName

/**

Response that contains the rain data for the past hour.*
@property lastHour Amount of rain in the past hour. Defaults to null if missing from JSON.*
@version 1.0*/
data class Rain(
    @SerializedName("1h") val lastHour: Double? = null
)
