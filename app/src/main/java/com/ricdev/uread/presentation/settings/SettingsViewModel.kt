package com.ricdev.uread.presentation.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ricdev.uread.data.model.AppLanguage
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.source.local.AppPreferencesUtil
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
class SettingsViewModel @Inject constructor(
    private val appPreferencesUtil: AppPreferencesUtil,
    private val languageHelper: LanguageHelper,
    application: Application,
) : AndroidViewModel(application) {


    private val _appPreferences = MutableStateFlow(AppPreferencesUtil.defaultPreferences)
    val appPreferences: StateFlow<AppPreferences> = _appPreferences.asStateFlow()


    init {
        viewModelScope.launch {
            appPreferencesUtil.appPreferencesFlow.first().let { initialPreferences ->
                _appPreferences.value = initialPreferences
            }

            // Continue collecting preferences updates
            appPreferencesUtil.appPreferencesFlow.collect { preferences ->
                _appPreferences.value = preferences
            }
        }
    }


    fun updatePdfSupport(isPdfSupported: Boolean) {
        viewModelScope.launch {
            appPreferencesUtil.updateAppPreferences(appPreferences.value.copy(enablePdfSupport = isPdfSupported))
        }
    }


    fun addScanDirectory(directory: String) {
        viewModelScope.launch {
            val currentDirectories = appPreferences.value.scanDirectories
            if (!currentDirectories.contains(directory)) {
                val updatedDirectories = currentDirectories + directory
                Log.d("it's me", "the Settings viewModel")
                appPreferencesUtil.updateAppPreferences(appPreferences.value.copy(scanDirectories = updatedDirectories))
            }
        }
    }

    fun removeScanDirectory(directory: String) {
        viewModelScope.launch {
            val updatedDirectories = appPreferences.value.scanDirectories - directory
            Log.d("it's me", "the Settings viewModel")
            appPreferencesUtil.updateAppPreferences(appPreferences.value.copy(scanDirectories = updatedDirectories))
        }
    }





    fun updateLanguage(languageCode: String) {
        viewModelScope.launch {
            val language = AppLanguage.fromCode(languageCode)
            appPreferencesUtil.updateAppPreferences(appPreferences.value.copy(language = language.code))
            languageHelper.changeLanguage(getApplication(), language)
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

    private fun updatePremiumStatus(isPremium: Boolean) {
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