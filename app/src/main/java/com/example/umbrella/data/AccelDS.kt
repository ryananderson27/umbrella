package com.example.umbrella.data

import com.example.umbrella.models.AccelReading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AccelDS {
    private val _latest = MutableStateFlow<AccelReading?>(null)
    val latest: StateFlow<AccelReading?> = _latest

    fun update(reading: AccelReading) {
        _latest.value = reading
    }
}