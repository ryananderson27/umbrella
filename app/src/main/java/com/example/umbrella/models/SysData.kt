package com.example.umbrella.models

import com.google.gson.annotations.SerializedName

/**

Response that contains the sunrise and sunset data.*
@property sunrise A timestamp indicating the sunrise time. Defaults to null if missing from JSON.
@property sunset A timestamp indicating the sunset time. Defaults to null if missing from JSON.*
@version 1.0*/
data class SysData(
    @SerializedName("sunrise") val sunrise: Long? = null,
    @SerializedName("sunset") val sunset: Long? = null
)
