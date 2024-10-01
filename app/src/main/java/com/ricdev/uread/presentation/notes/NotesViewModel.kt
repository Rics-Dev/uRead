package com.ricdev.uread.presentation.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.Note
import com.ricdev.uread.data.source.local.AppPreferencesUtil
import com.ricdev.uread.domain.use_case.books.GetAllBooksUseCase
import com.ricdev.uread.domain.use_case.notes.AddNoteUseCase
import com.ricdev.uread.domain.use_case.notes.DeleteNoteUseCase
import com.ricdev.uread.domain.use_case.notes.GetAllNotesUseCase
import com.ricdev.uread.domain.use_case.notes.GetNotesForBookUseCase
import com.ricdev.uread.domain.use_case.notes.UpdateNoteUseCase
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
class NotesViewModel @Inject constructor(
    private val appPreferencesUtil: AppPreferencesUtil,
    getAllBooksUseCase: GetAllBooksUseCase,
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val getNotesForBookUseCase: GetNotesForBookUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    application: Application,
) : AndroidViewModel(application){


    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _appPreferences = MutableStateFlow(AppPreferencesUtil.defaultPreferences)
    val appPreferences: StateFlow<AppPreferences> = _appPreferences.asStateFlow()


    @OptIn(ExperimentalCoroutinesApi::class)
    val booksWithNotes: Flow<List<BookWithNotes>> = getAllBooksUseCase()
        .flatMapLatest { books ->
            combine(books.map { book ->
                getNotesForBookUseCase(book.id).map { notes ->
                    BookWithNotes(book, notes)
                }
            }) { it.toList() }
        }
        .map { booksWithNotes ->
            booksWithNotes.filter { it.notes.isNotEmpty() }
        }


    init {
        loadAppPreferences()
        loadNotes()
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

    private fun loadNotes() {
        viewModelScope.launch {
            getAllNotesUseCase().collect { notesList ->
                _notes.value = notesList
            }
        }
    }

    fun addNote(note: Note) {
        viewModelScope.launch {
            addNoteUseCase(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            updateNoteUseCase(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            deleteNoteUseCase(note)
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
