package com.example.promptit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Define the dark color palette here
private val DarkColorPalette = darkColorScheme(
    primary = Black,
    onPrimary = White, // Text on top of primary will be white for contrast
    secondary = White,
    onSecondary = Black, // Assuming black text on white secondary color for contrast
    // Add other colors for dark theme if needed
)

// Define the light color palette here
private val LightColorPalette = lightColorScheme(
    primary = White, // Assuming a light primary color
    onPrimary = Black, // Text on top of primary will be black for contrast
    secondary = Black, // Assuming a dark secondary color
    onSecondary = White, // Text on top of secondary will be white for contrast
    // Add other colors for light theme if needed
)

@Composable
fun PromptItTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
