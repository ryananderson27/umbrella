package com.example.umbrella

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.umbrella.viewmodels.UserViewModel
import com.example.umbrella.viewmodels.WeatherViewModel

object AppDestinations {
    const val HOME = "home"
    const val CONTEXT = "context"
    const val PRIVACY = "privacy"
    const val SETTINGS = "settings"
}

@Composable
fun WeatherDisplay(
    weatherViewModel: WeatherViewModel,
    userViewModel: UserViewModel,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentRoute =
                    navController.currentBackStackEntryAsState().value?.destination?.route

                NavigationBarItem(
                    selected = currentRoute == AppDestinations.HOME,
                    onClick = { navController.navigate(AppDestinations.HOME) },
                    label = { Text("Home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
                )

                NavigationBarItem(
                    selected = currentRoute == AppDestinations.CONTEXT,
                    onClick = { navController.navigate(AppDestinations.CONTEXT) },
                    label = { Text("Context") },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Context") }
                )

                NavigationBarItem(
                    selected = currentRoute == AppDestinations.PRIVACY,
                    onClick = { navController.navigate(AppDestinations.PRIVACY) },
                    label = { Text("Privacy") },
                    icon = { Icon(Icons.Default.AccountBox, contentDescription = "Privacy") }
                )

                NavigationBarItem(
                    selected = currentRoute == AppDestinations.SETTINGS,
                    onClick = { navController.navigate(AppDestinations.SETTINGS) },
                    label = { Text("Settings") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") }
                )
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = AppDestinations.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(AppDestinations.HOME) {
                HomeScreen(weatherViewModel)
            }
            composable(AppDestinations.CONTEXT) { ContextLogicScreen() }
            composable(AppDestinations.PRIVACY) { PrivacyScreen() }
            composable(AppDestinations.SETTINGS) {
                SettingsScreen(userViewModel, isDarkMode, onToggleTheme)
            }
        }
    }
}

@Composable
fun HomeScreen(
    weatherViewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val weatherInfo by weatherViewModel.weatherInfo.collectAsState()
    val accelerometerData by weatherViewModel.accelerometerData.collectAsState()
    val conditionData by weatherViewModel.conditionData.collectAsState()
    val lastUpdated by weatherViewModel.lastUpdated.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Home", style = MaterialTheme.typography.headlineMedium)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Weather Information", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(weatherInfo, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Accelerometer Data", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("X-axis: ${accelerometerData ?: "No data"}", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Condition Advice", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(conditionData, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Last Updated", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(lastUpdated ?: "Never", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
fun ContextLogicScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Context Logic", style = MaterialTheme.typography.headlineMedium) }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Background Logic", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "The app runs an accelerometer service in the foreground and keeps the latest motion reading available to the WeatherViewModel. It also gets user location, pulls the current weather, and reads saved preferences like whether the user has an umbrella or snow shoes.",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Sensor Fusion", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "The sensor fusion combines motion data, weather data, and user settings to decide what message to show. For example, if the user moves quickly, the app checks the weather and can tell them to grab an umbrella or snow shoes depending on the conditions.",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun PrivacyScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Privacy", style = MaterialTheme.typography.headlineMedium) }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Location Permissions", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "User data is stored locally on the device using DataStore, so preferences like “has umbrella” and “has snow shoes” are not sent to any external server. The app only sends location coordinates to the OpenWeather API to fetch current conditions, and no personal identifiers are included in that request.",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    userViewModel: UserViewModel,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    val userPrefs by userViewModel.prefs.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Settings", style = MaterialTheme.typography.headlineMedium) }
        item { Text("Location: ${userPrefs.locationName}", color = MaterialTheme.colorScheme.onBackground) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dark Mode", color = MaterialTheme.colorScheme.onBackground)
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { onToggleTheme() }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Has Umbrella", color = MaterialTheme.colorScheme.onBackground)
                Switch(
                    checked = userPrefs.hasUmbrella,
                    onCheckedChange = { userViewModel.setHasUmbrella(it) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Has Snow Shoes", color = MaterialTheme.colorScheme.onBackground)
                Switch(
                    checked = userPrefs.hasSnowShoes,
                    onCheckedChange = { userViewModel.setHasSnowShoes(it) }
                )
            }
        }
    }
}