package com.ricdev.uread.presentation.annotations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.BookAnnotation
import com.ricdev.uread.data.model.Note
import com.ricdev.uread.data.source.local.AppPreferencesUtil
import com.ricdev.uread.domain.use_case.annotations.DeleteAnnotationUseCase
import com.ricdev.uread.domain.use_case.annotations.GetAnnotationsUseCase
import com.ricdev.uread.domain.use_case.annotations.UpdateAnnotationUseCase
import com.ricdev.uread.domain.use_case.books.GetAllBooksUseCase
import com.ricdev.uread.util.PurchaseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnnotationsViewModel @Inject constructor(
    private val appPreferencesUtil: AppPreferencesUtil,
    getAllBooksUseCase: GetAllBooksUseCase,
    private val getAnnotationsUseCase: GetAnnotationsUseCase,
    private val removeAnnotationUseCase: DeleteAnnotationUseCase,
    private val updateAnnotationUseCase: UpdateAnnotationUseCase,
    application: Application,
) : AndroidViewModel(application){


    private val _annotations = MutableStateFlow<List<Note>>(emptyList())
    val annotations: StateFlow<List<Note>> = _annotations.asStateFlow()

    private val _appPreferences = MutableStateFlow(AppPreferencesUtil.defaultPreferences)
    val appPreferences: StateFlow<AppPreferences> = _appPreferences.asStateFlow()


    @OptIn(ExperimentalCoroutinesApi::class)
    val booksWithAnnotations: Flow<List<BookWithAnnotations>> = getAllBooksUseCase()
        .flatMapLatest { books ->
            combine(books.map { book ->
                getAnnotationsUseCase(book.id).map { annotations ->
                    BookWithAnnotations(book, annotations)
                }
            }) { it.toList() }
        }
        .map { bookWithAnnotations ->
            bookWithAnnotations.filter { it.annotation.isNotEmpty() }
        }


    init {
        loadAppPreferences()
    }


    private fun loadAppPreferences(){
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


    fun removeAnnotation(annotation: BookAnnotation){
        viewModelScope.launch {
            removeAnnotationUseCase(annotation)
        }
    }


    fun updateAnnotation(updatedAnnotation: BookAnnotation){
        viewModelScope.launch {
            updateAnnotationUseCase(updatedAnnotation)
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