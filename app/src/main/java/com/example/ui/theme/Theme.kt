package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val ComfortColorScheme = lightColorScheme(
    primary = ComfortPrimary,
    onPrimary = ComfortOnPrimary,
    primaryContainer = ComfortPrimaryContainer,
    onPrimaryContainer = ComfortOnPrimaryContainer,
    background = ComfortBackground,
    onBackground = ComfortOnBackground,
    surface = ComfortSurface,
    onSurface = ComfortOnSurface,
    surfaceVariant = ComfortSurfaceVariant,
    onSurfaceVariant = ComfortOnSurfaceVariant,
    secondary = ComfortSecondary,
    onSecondary = ComfortOnSecondary
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, 
  dynamicColor: Boolean = false, 
  content: @Composable () -> Unit,
) {
  val colorScheme = ComfortColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
