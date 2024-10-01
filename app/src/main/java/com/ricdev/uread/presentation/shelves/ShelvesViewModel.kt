package com.ricdev.uread.presentation.shelves

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.Shelf
import com.ricdev.uread.data.source.local.AppPreferencesUtil
import com.ricdev.uread.domain.use_case.shelves.AddShelfUseCase
import com.ricdev.uread.domain.use_case.shelves.GetShelvesUseCase
import com.ricdev.uread.domain.use_case.shelves.RemoveShelfUseCase
import com.ricdev.uread.domain.use_case.shelves.UpdateShelfUseCase
import com.ricdev.uread.util.PurchaseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ShelvesViewModel @Inject constructor(
    private val appPreferencesUtil: AppPreferencesUtil,
    private val getShelvesUseCase: GetShelvesUseCase,
    private val addShelfUseCase: AddShelfUseCase,
    private val updateShelfUseCase: UpdateShelfUseCase,
    private val removeShelfUseCase: RemoveShelfUseCase,
    application: Application,
) : AndroidViewModel(application) {

    private val _shelvesState = MutableStateFlow<ShelvesState>(ShelvesState.Loading)
    val shelvesState: StateFlow<ShelvesState> = _shelvesState.asStateFlow()

    private val _appPreferences = MutableStateFlow(AppPreferencesUtil.defaultPreferences)
    val appPreferences: StateFlow<AppPreferences> = _appPreferences.asStateFlow()

    init {
        getShelves()
    }


    private fun getShelves() {
        viewModelScope.launch {
            appPreferencesUtil.appPreferencesFlow.first().let { initialPreferences ->
                _appPreferences.value = initialPreferences
            }
            try {
                getShelvesUseCase().collect { shelf ->
                    _shelvesState.value = ShelvesState.Success(shelf)
                }
            } catch (e: Exception) {
                _shelvesState.value = ShelvesState.Error(e.message ?: "Unknown error occurred")
            }
            // Continue collecting preferences updates
            appPreferencesUtil.appPreferencesFlow.collect { preferences ->
                _appPreferences.value = preferences
            }
        }
    }


    fun addShelf(shelfName: String) {
        viewModelScope.launch {
            try {
                val currentShelves =
                    (shelvesState.value as? ShelvesState.Success)?.shelves ?: emptyList()
                val newOrder = currentShelves.size
                addShelfUseCase(shelfName, newOrder)
                getShelves()
            } catch (e: Exception) {
                _shelvesState.value = ShelvesState.Error("Failed to add shelf: ${e.message}")
            }
        }
    }


    fun updateShelf(newShelf: Shelf) {
        viewModelScope.launch {
            try {
                updateShelfUseCase(newShelf)
                getShelves()
            } catch (e: Exception) {
                _shelvesState.value = ShelvesState.Error("Failed to update shelf: ${e.message}")
            }
        }
    }

    fun deleteShelf(removedShelf: Shelf) {
        viewModelScope.launch {
            try {

                removeShelfUseCase(removedShelf)
                getShelves()
            } catch (e: Exception) {
                _shelvesState.value = ShelvesState.Error("Failed to delete shelf: ${e.message}")
            }
        }
    }

    fun moveShelf(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            try {
                val currentShelves =
                    (shelvesState.value as? ShelvesState.Success)?.shelves?.toMutableList()
                        ?: return@launch
                if (fromIndex < 0 || fromIndex >= currentShelves.size || toIndex < 0 || toIndex >= currentShelves.size) {
                    return@launch
                }
                val shelf = currentShelves.removeAt(fromIndex)
                currentShelves.add(toIndex, shelf)
                currentShelves.forEachIndexed { index, currentShelf ->
                    updateShelfUseCase(currentShelf.copy(order = index))
                }
                getShelves()
            } catch (e: Exception) {
                _shelvesState.value = ShelvesState.Error("Failed to move shelf: ${e.message}")
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