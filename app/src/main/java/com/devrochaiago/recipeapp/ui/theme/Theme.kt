package com.devrochaiago.recipeapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryOrange,
    secondary = PrimaryYellow,
    tertiary = PrimaryDark,

    background = BackgroundDark,
    surface = SurfaceDark,

    onPrimary = TextLight,       // Texto em cima da cor primária (botões)
    onSecondary = TextDark,      // Texto em cima da cor secundária
    onBackground = TextLight,    // Texto padrão em cima do fundo escuro
    onSurface = TextLight,       // Texto em cima dos cards (Surface)
    onSurfaceVariant = TextGray  // Textos secundários (ex: descrição, medidas)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    secondary = PrimaryYellow,
    tertiary = PrimaryDark,

    background = BackgroundLight,
    surface = SurfaceLight,

    onPrimary = TextLight,
    onSecondary = TextDark,
    onBackground = TextDark,
    onSurface = TextDark,
    onSurfaceVariant = TextGray
)

@Composable
fun RecipeAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}