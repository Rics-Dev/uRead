package com.ricdev.uread.presentation.settings.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    application: Application,
): AndroidViewModel(application) {

    private val _isDarkTheme = MutableStateFlow<Boolean?>(null)
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme.asStateFlow()




    init {
        val isDarkThemeString = savedStateHandle.get<String>("isDarkTheme")
        _isDarkTheme.value = isDarkThemeString?.toBoolean()

    }




}