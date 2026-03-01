package com.example.umbrella.data

import com.example.umbrella.models.AccelerometerReading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AccelerometerDataStore {
    private val _latest = MutableStateFlow<AccelerometerReading?>(null)
    val latest: StateFlow<AccelerometerReading?> = _latest

    fun update(reading: AccelerometerReading) {
        _latest.value = reading
    }
}