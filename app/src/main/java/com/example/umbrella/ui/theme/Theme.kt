package com.example.umbrella.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color


private val LightColors = lightColorScheme(
    primary = UmbrellaBlue,
    secondary = UmbrellaLightBlue,
    background = Color.White,
    surface = UmbrellaGray,
    onPrimary = Color.White,
    onBackground = Color.Black
)

private val DarkColors = darkColorScheme(
    primary = UmbrellaDarkBlue,
    secondary = UmbrellaLightBlue,
    background = Color.Black,
    surface = UmbrellaGray,
    onPrimary = Color.White,
    onBackground = Color.White
)

@Composable
fun UmbrellaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = UmbrellaTypography,
        content = content
    )
}