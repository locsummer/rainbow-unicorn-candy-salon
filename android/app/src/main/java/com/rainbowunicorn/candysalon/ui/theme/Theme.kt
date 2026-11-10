package com.rainbowunicorn.candysalon.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val CandyPink = Color(0xFFFF5996)
val CandyPastelPink = Color(0xFFFFB6C1)
val CandyPurple = Color(0xFFC792EA)
val CandyMint = Color(0xFF7CE8D5)
val CandyYellow = Color(0xFFFFE57F)
val CandyOrange = Color(0xFFFFB74D)
val CandyCream = Color(0xFFFFF9FB)
val CandyDeepPlum = Color(0xFF4A154B)
val CandyDarkBg = Color(0xFF2C1035)

val RainbowColorScheme = lightColorScheme(
    primary = CandyPink,
    onPrimary = Color.White,
    primaryContainer = CandyPastelPink,
    onPrimaryContainer = CandyDeepPlum,
    secondary = CandyPurple,
    onSecondary = Color.White,
    secondaryContainer = CandyYellow,
    onSecondaryContainer = CandyDeepPlum,
    tertiary = CandyMint,
    background = CandyCream,
    onBackground = CandyDeepPlum,
    surface = Color.White,
    onSurface = CandyDeepPlum
)

val CandyTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = CandyDeepPlum
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        color = CandyDeepPlum
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        color = CandyDeepPlum
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
)
