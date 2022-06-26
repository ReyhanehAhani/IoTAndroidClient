package com.reyhaneh.ahani.iotclient.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = Color(0xFF4e7932),
    primaryVariant = Color(0xFF679940),
    secondary = Color(0xFFa3ce62),
    secondaryVariant = Color(0xFF8dc73f),
)

private val LightColorPalette = lightColors(
    primary = Color(0xFF4e7932),
    primaryVariant = Color(0xFF679940),
    secondary = Color(0xFFa3ce62),
    secondaryVariant = Color(0xFF8dc73f),

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun IoTClientTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )

    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = DarkColorPalette.primaryVariant)
}