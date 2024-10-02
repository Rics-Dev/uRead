package com.ricdev.uread

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.source.local.AppPreferencesUtil
import com.ricdev.uread.navigation.Screens
import com.ricdev.uread.util.LanguageHelper
import com.ricdev.uread.util.PurchaseHelper
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


//    private val _isLoading = MutableStateFlow(true)
//    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    private val _appPreferences = MutableStateFlow(AppPreferencesUtil.defaultPreferences)
    val appPreferences: StateFlow<AppPreferences> = _appPreferences.asStateFlow()



//    private val _startDestination = MutableStateFlow(Screens.GettingStartedScreen.route)
//    val startDestination: StateFlow<String> = _startDestination.asStateFlow()

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()



    init {
        viewModelScope.launch {
            try {
                val initialPreferences = appPreferencesUtil.appPreferencesFlow.first()
                determineStartDestination(initialPreferences)
                _appPreferences.value = initialPreferences
                languageHelper.changeLanguage(getApplication(), initialPreferences.language)
            } catch (e: Exception) {
                // Handle potential exceptions (e.g., log error, show error message)
            }
        }

        viewModelScope.launch {
            appPreferencesUtil.appPreferencesFlow.collect { preferences ->
                _appPreferences.value = preferences
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


    fun purchasePremium(purchaseHelper: PurchaseHelper) {
        purchaseHelper.makePurchase()
        viewModelScope.launch {
            purchaseHelper.isPremium.collect { isPremium ->
                updatePremiumStatus(isPremium)
            }
        }
    }

    fun updatePremiumStatus(isPremium: Boolean) {
        viewModelScope.launch {
            val currentPreferences = appPreferences.value
            if (currentPreferences.isPremium != isPremium) {
                val updatedPreferences = currentPreferences.copy(isPremium = isPremium)
                appPreferencesUtil.updateAppPreferences(updatedPreferences)
                _appPreferences.value = updatedPreferences
            }
        }
    }

}



