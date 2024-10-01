package com.ricdev.uread

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
class MainViewModel @Inject constructor(
    private val appPreferencesUtil: AppPreferencesUtil,
    private val languageHelper: LanguageHelper,
    application: Application,
) : AndroidViewModel(application) {

    private val _appPreferences = MutableStateFlow(AppPreferencesUtil.defaultPreferences)
    val appPreferences: StateFlow<AppPreferences> = _appPreferences.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    init {
        viewModelScope.launch {
            appPreferencesUtil.appPreferencesFlow.first().let { initialPreferences ->
                _appPreferences.value = initialPreferences
                languageHelper.changeLanguage(getApplication(), initialPreferences.language)
                _isReady.value = true
            }

            // Continue collecting preferences updates
            appPreferencesUtil.appPreferencesFlow.collect { preferences ->
                _appPreferences.value = preferences
            }

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



