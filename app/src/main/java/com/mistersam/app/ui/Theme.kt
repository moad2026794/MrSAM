package com.mistersam.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(
    primary = Color(0xFF1F6F5C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD5EFE6),
    onPrimaryContainer = Color(0xFF0B3D31),
    secondary = Color(0xFFB9731A),
    secondaryContainer = Color(0xFFFFE8C7),
    background = Color(0xFFFAF7F2),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFEFEAE0)
)

private val Dark = darkColorScheme(
    primary = Color(0xFF7FD1B9),
    onPrimary = Color(0xFF00382C),
    primaryContainer = Color(0xFF0F4F40),
    onPrimaryContainer = Color(0xFFD5EFE6),
    secondary = Color(0xFFF0B766),
    secondaryContainer = Color(0xFF5A3A08),
    background = Color(0xFF121614),
    surface = Color(0xFF1A1F1D),
    surfaceVariant = Color(0xFF2A302D)
)

@Composable
fun SamTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) Dark else Light,
        content = content
    )
}
