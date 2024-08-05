package com.example.uread.data.source.local

import android.content.Context

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.uread.data.model.ReaderPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "reader_preferences")

class ReaderPreferencesUtil @Inject constructor(
    context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val FONT_SIZE = doublePreferencesKey("font_size")
        val PAGE_MARGINS = doublePreferencesKey("page_margins")
        val SCROLL = booleanPreferencesKey("scroll")
        val BACKGROUND_COLOR = intPreferencesKey("background_color")
        val TEXT_COLOR = intPreferencesKey("text_color")

        // Default values
        val defaultPreferences = ReaderPreferences(
            fontSize = 1.0,
            pageMargins = 1.4,
            scroll = false,
            backgroundColor = Color.White,
            textColor = Color.Black
        )
    }

    val readerPreferencesFlow: Flow<ReaderPreferences> = dataStore.data.map { preferences ->
        ReaderPreferences(
            fontSize = preferences[FONT_SIZE] ?: defaultPreferences.fontSize,
            pageMargins = preferences[PAGE_MARGINS] ?: defaultPreferences.pageMargins,
            scroll = preferences[SCROLL] ?: defaultPreferences.scroll,
            backgroundColor = Color(preferences[BACKGROUND_COLOR] ?: defaultPreferences.backgroundColor.toArgb()),
            textColor = Color(preferences[TEXT_COLOR] ?: defaultPreferences.textColor.toArgb())
        )
    }

    suspend fun updatePreferences(newPreferences: ReaderPreferences) {
        dataStore.edit { preferences ->
            preferences[FONT_SIZE] = newPreferences.fontSize
            preferences[PAGE_MARGINS] = newPreferences.pageMargins
            preferences[SCROLL] = newPreferences.scroll
            preferences[BACKGROUND_COLOR] = newPreferences.backgroundColor.toArgb()
            preferences[TEXT_COLOR] = newPreferences.textColor.toArgb()
        }
    }
}
