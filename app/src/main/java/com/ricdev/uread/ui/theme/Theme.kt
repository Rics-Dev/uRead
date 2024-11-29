package com.ricdev.uread.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ricdev.uread.R
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.AppTheme


@Composable
fun UReadTheme(
    viewModel: AppThemeViewModel = hiltViewModel(),
    content: @Composable () -> Unit,
) {

    val context = LocalContext.current
    val appPreferences by viewModel.appPreferences.collectAsStateWithLifecycle()


    val darkTheme = when (appPreferences.appTheme) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }
    val colorScheme = when {
        appPreferences.colorScheme == "Dynamic" -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                if (darkTheme) DarkColorScheme else LightColorScheme
            }
        }
        darkTheme -> when (appPreferences.colorScheme) {
            "Light", "Dark" -> DarkColorScheme
            "Light Grey", "Dark Grey" -> DarkGreyScheme
            "Light Sepia", "Dark Sepia" -> DarkSepiaScheme
            "Light Parchment", "Dark Parchment" -> DarkParchmentScheme
            "Light Yellow", "Dark Yellow" -> DarkYellowScheme
            "Light Teal", "Dark Teal" -> DarkTealScheme
            "Light Blue", "Dark Blue" -> DarkBlueScheme
            "Light Pink", "Dark Pink" -> DarkPinkScheme
            "Light Purple", "Dark Purple" -> DarkPurpleScheme
            "Light Red", "Dark Red" -> DarkRedScheme
            "Light Green", "Dark Green" -> DarkGreenScheme
            else -> DarkColorScheme
        }
        else -> when (appPreferences.colorScheme) {
            "Light", "Dark" -> LightColorScheme
            "Light Grey", "Dark Grey" -> LightGreyScheme
            "Light Sepia", "Dark Sepia" -> LightSepiaScheme
            "Light Parchment", "Dark Parchment" -> LightParchmentScheme
            "Light Yellow", "Dark Yellow" -> LightYellowScheme
            "Light Teal", "Dark Teal" -> LightTealScheme
            "Light Blue", "Dark Blue" -> LightBlueScheme
            "Light Pink", "Dark Pink" -> LightPinkScheme
            "Light Purple", "Dark Purple" -> LightPurpleScheme
            "Light Red", "Dark Red" -> LightRedScheme
            "Light Green", "Dark Green" -> LightGreenScheme
            else -> LightColorScheme
        }
    }

    val view = LocalView.current
    val activity = LocalContext.current as Activity


    // Set splash screen theme
    val splashScreenTheme = if (darkTheme) {
        R.style.Theme_App_Starting_Dark
    } else {
        R.style.Theme_App_Starting_Light
    }
    activity.setTheme(splashScreenTheme)



    SideEffect {
        val window = activity.window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowInsetsControllerCompat(window, view)

        windowInsetsController.isAppearanceLightStatusBars = !darkTheme
        windowInsetsController.isAppearanceLightNavigationBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}