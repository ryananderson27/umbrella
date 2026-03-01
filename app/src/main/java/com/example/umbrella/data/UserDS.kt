package com.example.umbrella.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userWeatherDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_weather_prefs"
)

class UserWeatherDataStore(private val context: Context) {

    companion object {
        private val LOCATION_NAME = stringPreferencesKey("location_name")
        private val LATITUDE = doublePreferencesKey("latitude")
        private val LONGITUDE = doublePreferencesKey("longitude")
        private val HAS_UMBRELLA = booleanPreferencesKey("has_umbrella")
        private val HAS_SNOW_SHOES = booleanPreferencesKey("has_snow_shoes")
    }

    val prefsFlow: Flow<UserWeatherPrefs> = context.userWeatherDataStore.data.map { prefs ->
        UserWeatherPrefs(
            locationName = prefs[LOCATION_NAME] ?: "",
            latitude = prefs[LATITUDE] ?: 0.0,
            longitude = prefs[LONGITUDE] ?: 0.0,
            hasUmbrella = prefs[HAS_UMBRELLA] ?: false,
            hasSnowShoes = prefs[HAS_SNOW_SHOES] ?: false
        )
    }

    suspend fun saveLocation(locationName: String, latitude: Double, longitude: Double) {
        context.userWeatherDataStore.edit { prefs ->
            prefs[LOCATION_NAME] = locationName
            prefs[LATITUDE] = latitude
            prefs[LONGITUDE] = longitude
        }
    }

    suspend fun setHasUmbrella(value: Boolean) {
        context.userWeatherDataStore.edit { prefs ->
            prefs[HAS_UMBRELLA] = value
        }
    }

    suspend fun setHasSnowShoes(value: Boolean) {
        context.userWeatherDataStore.edit { prefs ->
            prefs[HAS_SNOW_SHOES] = value
        }
    }

    suspend fun saveAll(
        locationName: String,
        latitude: Double,
        longitude: Double,
        hasUmbrella: Boolean,
        hasSnowShoes: Boolean
    ) {
        context.userWeatherDataStore.edit { prefs ->
            prefs[LOCATION_NAME] = locationName
            prefs[LATITUDE] = latitude
            prefs[LONGITUDE] = longitude
            prefs[HAS_UMBRELLA] = hasUmbrella
            prefs[HAS_SNOW_SHOES] = hasSnowShoes
        }
    }

    suspend fun clearAll() {
        context.userWeatherDataStore.edit { prefs ->
            prefs.clear()
        }
    }
}