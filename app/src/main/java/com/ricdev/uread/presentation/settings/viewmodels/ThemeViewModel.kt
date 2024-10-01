package com.ricdev.uread.presentation.settings.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.source.local.AppPreferencesUtil
import com.ricdev.uread.util.PurchaseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val appPreferencesUtil: AppPreferencesUtil,
    application: Application,
) : AndroidViewModel(application) {






    private val _appPreferences = MutableStateFlow(AppPreferencesUtil.defaultPreferences)
    val appPreferences: StateFlow<AppPreferences> = _appPreferences.asStateFlow()


    init {
        observeAppPreferences()
    }


    private fun observeAppPreferences() {
        viewModelScope.launch {
            appPreferencesUtil.appPreferencesFlow.collect { preferences ->
                _appPreferences.value = preferences
            }
        }
    }






    fun updateAppPreferences(newPreferences: AppPreferences) {
        viewModelScope.launch {
            appPreferencesUtil.updateAppPreferences(newPreferences)

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