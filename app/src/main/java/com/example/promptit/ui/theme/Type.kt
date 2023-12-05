package com.example.promptit.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.promptit.R

// Replace "notoserifmedium" with the actual file name of your font
val NotoSerif = FontFamily(Font(R.font.notoserifmedium))

// Set up the Typography
val Typography = Typography(
        displayLarge = TextStyle(
                fontFamily = NotoSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 60.sp
        ),
        displayMedium = TextStyle(
                fontFamily = NotoSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 50.sp
        ),
        displaySmall = TextStyle(
                fontFamily = NotoSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 46.sp
        ),
        headlineLarge = TextStyle(
                fontFamily = NotoSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 44.sp
        ),
        headlineMedium = TextStyle(
                fontFamily = NotoSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 42.sp
        ),
        headlineSmall = TextStyle(
                fontFamily = NotoSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 42.sp
        ),
        titleLarge = TextStyle(
                fontFamily = NotoSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 42.sp
        ),
        titleMedium = TextStyle(
                fontFamily = NotoSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 42.sp
        ),
        titleSmall = TextStyle(
                fontFamily = NotoSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 42.sp
        ),
        // Add other default text styles as needed
        // ...
)

