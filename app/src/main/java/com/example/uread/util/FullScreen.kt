package com.example.uread.util

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun SetFullScreen(context: Context) {
    val window = (context as? Activity)?.window
    val windowInsetsController = WindowCompat.getInsetsController(window!!, window.decorView)

    DisposableEffect(Unit) {
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        onDispose {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}