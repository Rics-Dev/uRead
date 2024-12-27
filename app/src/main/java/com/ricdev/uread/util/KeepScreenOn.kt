package com.ricdev.uread.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView

@Composable
fun KeepScreenOn(keepScreenOn: Boolean) {
    val currentView = LocalView.current
    DisposableEffect(keepScreenOn) {
        currentView.keepScreenOn = keepScreenOn
        onDispose { currentView.keepScreenOn = false }
    }
}