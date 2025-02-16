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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


//    init {
//        viewModelScope.launch {
//            try {
//                val initialPreferences = appPreferencesUtil.appPreferencesFlow.first()
//                Log.d("SplashViewModel", "Initial preferences: $initialPreferences")
//                _appPreferences.value = initialPreferences
//                languageHelper.changeLanguage(
//                    getApplication(),
//                    AppLanguage.fromCode(initialPreferences.language)
//                )
//            } catch (e: Exception) {
//                Log.e("SplashViewModel", "Initialization error", e)
//            }
//        }
//    }



        private val initializationJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                // Load initial preferences but DON'T determine destination yet
                val initialPreferences = appPreferencesUtil.appPreferencesFlow.first()

                withContext(Dispatchers.Main) {
                    _appPreferences.value = initialPreferences
                    languageHelper.changeLanguage(
                        getApplication(),
                        AppLanguage.fromCode(initialPreferences.language)
                    )
                }
            } catch (e: Exception) {
                Log.e("SplashViewModel", "Initialization error", e)
                _isLoading.value = false
            }
        }

        fun updatePremiumStatus(isPremium: Boolean) {
            viewModelScope.launch(Dispatchers.IO) {
                // Wait for initial preferences load to complete
                initializationJob.join()

                // Update premium status
                val currentPreferences = _appPreferences.value
                val updatedPreferences = currentPreferences.copy(isPremium = isPremium)

                if (currentPreferences.isPremium != isPremium) {
                    appPreferencesUtil.updateAppPreferences(updatedPreferences)
                    _appPreferences.value = updatedPreferences
                }

                // FINALLY determine destination after premium status update
                withContext(Dispatchers.Main) {
                    determineStartDestination(updatedPreferences)
                }
            }
        }

        private fun determineStartDestination(prefs: AppPreferences) {
            _startDestination.value = if (prefs.isFirstLaunch) {
                Screens.GettingStartedScreen.route
            } else {
                Screens.HomeScreen.route
            }
            _isLoading.value = false
        }


//    fun updatePremiumStatus(isPremium: Boolean) {
//        viewModelScope.launch {
//            val currentPreferences = _appPreferences.value
//            val updatedPreferences = currentPreferences.copy(isPremium = isPremium)
//            if (currentPreferences.isPremium != isPremium) {
//                appPreferencesUtil.updateAppPreferences(updatedPreferences)
//                _appPreferences.value = updatedPreferences
//            }
//            determineStartDestination(updatedPreferences)
//        }
//    }
}



