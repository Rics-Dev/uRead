package com.ricdev.uread

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ricdev.uread.data.model.AppLanguage
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.source.local.AppPreferencesUtil
import com.ricdev.uread.navigation.Screens
import com.ricdev.uread.util.LanguageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val appPreferencesUtil: AppPreferencesUtil,
    private val languageHelper: LanguageHelper,
    application: Application,
) : AndroidViewModel(application) {


    private val _appPreferences = MutableStateFlow(AppPreferencesUtil.defaultPreferences)
    val appPreferences: StateFlow<AppPreferences> = _appPreferences.asStateFlow()

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()


    init {
        viewModelScope.launch {
            try {
                val initialPreferences = appPreferencesUtil.appPreferencesFlow.first()
                Log.d("SplashViewModel", "Initial preferences: $initialPreferences")
                determineStartDestination(initialPreferences)
                _appPreferences.value = initialPreferences
                languageHelper.changeLanguage(
                    getApplication(),
                    AppLanguage.fromCode(initialPreferences.language)
                )
            } catch (e: Exception) {
                Log.e("SplashViewModel", "Initialization error", e)
            }
        }
    }

    private fun determineStartDestination(preferences: AppPreferences) {
        _startDestination.value = if (preferences.isFirstLaunch) {
            Screens.GettingStartedScreen.route
        } else {
            Screens.HomeScreen.route
        }
    }


    fun updatePremiumStatus(isPremium: Boolean) {
        viewModelScope.launch {
            val currentPreferences = appPreferencesUtil.appPreferencesFlow.first()
            if (currentPreferences.isPremium != isPremium) {
                val updatedPreferences = currentPreferences.copy(isPremium = isPremium)
                appPreferencesUtil.updateAppPreferences(updatedPreferences)
                _appPreferences.value = updatedPreferences
            }
        }
    }
}



