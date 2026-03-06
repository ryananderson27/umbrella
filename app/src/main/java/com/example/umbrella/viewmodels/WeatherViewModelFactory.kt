package com.example.umbrella.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.umbrella.data.WeatherRepository

class WeatherViewModelFactory(
    private val api: WeatherRepository,
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(api, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}