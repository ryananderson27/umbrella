package com.example.umbrella.viewmodels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umbrella.data.UserWeatherDataStore
import com.example.umbrella.models.UserWeatherPrefs
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class UserViewModel(private val dataStore: UserWeatherDataStore) : ViewModel() {
    val prefs = dataStore.prefsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserWeatherPrefs("", false, false)
    )

    fun setHasUmbrella(value: Boolean) {
       viewModelScope.launch {
           dataStore.setHasUmbrella(value)
       }
    }

    fun setHasSnowShoes(value: Boolean) {
        viewModelScope.launch {
            dataStore.setHasSnowShoes(value)
        }
    }

    fun saveLocation(name: String) {
        viewModelScope.launch {
            dataStore.saveLocation(name)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            dataStore.clearAll()
        }
    }
}