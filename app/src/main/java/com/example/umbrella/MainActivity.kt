package com.example.umbrella

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.umbrella.data.AccelerometerForegroundService
import com.example.umbrella.ui.theme.UmbrellaTheme
import com.example.umbrella.viewmodels.WeatherViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.umbrella.data.WeatherRepository
import com.example.umbrella.viewmodels.WeatherViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AccelerometerForegroundService.start(this)
        enableEdgeToEdge()
        setContent {
            MyApplicationApp()
        }
    }
}

@Composable
fun MyApplicationApp() {
    val context = LocalContext.current.applicationContext as Application
    val weatherRepository = WeatherRepository() // Replace with actual repo if needed

    val weatherViewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(weatherRepository, context)
    )

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var isDarkMode by rememberSaveable { mutableStateOf(false) }

    UmbrellaTheme(darkTheme = isDarkMode) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                AppDestinations.entries.forEach { item ->
                    item(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = item == currentDestination,
                        onClick = { currentDestination = item }
                    )
                }
            }
        ) {
            when (currentDestination) {
                AppDestinations.HOME -> HomeScreen(viewModel = weatherViewModel)
                AppDestinations.CONTEXT -> ContextScreen()
                AppDestinations.PRIVACY -> PrivacyScreen()
                AppDestinations.SETTINGS -> SettingsScreen(
                    isDarkMode = isDarkMode,
                    onToggleTheme = { isDarkMode = !isDarkMode }
                )
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    CONTEXT("Context Logic", Icons.Default.Info),
    PRIVACY("Privacy", Icons.Default.AccountBox),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel
) {
    val weather by viewModel.weatherInfo.collectAsState()
    val accelerometer by viewModel.accelerometerData.collectAsState(initial = 0f)
    val condition by viewModel.conditionData.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Weather Information", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(weather)
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Accelerometer Data", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("X-axis: $accelerometer")
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Condition Advice", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(condition)
            }
        }
    }
}


@Composable
fun ContextScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        Text("Background Logic", modifier = modifier)
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("This will explain the background logic", modifier = modifier)
        }

        Text("Sensor Fusion", modifier = modifier)
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("This will explain the sensor fusion", modifier = modifier)
        }
    }
}

@Composable
fun PrivacyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        Text("Why we need location", modifier = modifier)
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("This will explain the why we need location", modifier = modifier)
        }
    }
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Dark Mode",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Switch(
            checked = isDarkMode,
            onCheckedChange = { onToggleTheme() },
            modifier = Modifier.scale(1.3f)
        )
    }
}
