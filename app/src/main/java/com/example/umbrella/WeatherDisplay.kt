package com.example.umbrella

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.umbrella.viewmodels.UserViewModel
import com.example.umbrella.viewmodels.WeatherViewModel

@Composable
fun WeatherDisplay(
    weatherViewModel: WeatherViewModel,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    val userPrefs by userViewModel.prefs.collectAsState()
    val weatherInfo by weatherViewModel.weatherInfo.collectAsState()
    val accelerometerData by weatherViewModel.accelerometerData.collectAsState()
    val conditionData by weatherViewModel.conditionData.collectAsState()
    val lastUpdated by weatherViewModel.lastUpdated.collectAsState()

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Location: ${userPrefs.locationName}")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Has Umbrella")
            Switch(
                checked = userPrefs.hasUmbrella,
                onCheckedChange = { newValue ->
                    userViewModel.setHasUmbrella(newValue)
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Has Snow Shoes")
            Switch(
                checked = userPrefs.hasSnowShoes,
                onCheckedChange = { newValue ->
                    userViewModel.setHasSnowShoes(newValue)
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Weather: $weatherInfo")
        Text(text = "Accelerometer X: ${accelerometerData ?: "No data"}")
        Text(text = "Condition: $conditionData")
        Text(text = "Last Updated: ${lastUpdated ?: "Never"}")
    }
}