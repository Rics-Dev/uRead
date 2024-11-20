package com.ricdev.uread.data.source.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.AppTheme
import com.ricdev.uread.data.model.FileType
import com.ricdev.uread.data.model.Layout
import com.ricdev.uread.data.model.ReadingStatus
import com.ricdev.uread.data.model.SortOption
import com.ricdev.uread.data.model.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "app_prefs")

class AppPreferencesUtil @Inject constructor(
    context: Context
) {
    private val dataStore = context.dataStore


    companion object {
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val SCAN_DIRECTORY = stringSetPreferencesKey("scan_directory")
        val ENABLE_PDF_SUPPORT = booleanPreferencesKey("enable_pdf_support")
        val LANGUAGE = stringPreferencesKey("language")
        val APP_THEME = stringPreferencesKey("app_theme")
        val COLOR_SCHEME = stringPreferencesKey("color_scheme")
        val HOME_LAYOUT = stringPreferencesKey("home_layout")
        val GRID_COUNT = intPreferencesKey("grid_count")
        val SHOW_ENTRIES = booleanPreferencesKey("show_entries")
        val SHOW_RATING = booleanPreferencesKey("show_rating")
        val SHOW_READING_STATUS = booleanPreferencesKey("show_reading_status")
        val SHOW_READING_DATES = booleanPreferencesKey("show_reading_dates")
        val SHOW_PDF_LABEL = booleanPreferencesKey("show_pdf_label")
        val SORT_BY = stringPreferencesKey("sort_by")
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val READING_STATUS = stringSetPreferencesKey("reading_status")
        val FILE_TYPE = stringSetPreferencesKey("file_type")
        val IS_PREMIUM = booleanPreferencesKey("is_premium")



        // Default values
        val defaultPreferences = AppPreferences(
            isFirstLaunch = true,
            scanDirectories = emptySet(),
            enablePdfSupport = true,
            language = "system",
            appTheme = AppTheme.SYSTEM,
            colorScheme = "Dynamic",
            homeLayout = Layout.Grid,
            gridCount = 4,
            showEntries = false,
            showRating = false,
            showReadingStatus = false,
            showReadingDates = false,
            showPdfLabel = true,
            sortBy = SortOption.LAST_ADDED,
            sortOrder = SortOrder.ASCENDING,
            readingStatus = emptySet(),
            fileTypes = emptySet(),
            isPremium = false,
        )
    }


    val appPreferencesFlow: Flow<AppPreferences> = dataStore.data.map { preferences ->
        AppPreferences(
            isFirstLaunch = preferences[IS_FIRST_LAUNCH] ?: defaultPreferences.isFirstLaunch,
            scanDirectories = preferences[SCAN_DIRECTORY] ?: defaultPreferences.scanDirectories,
            enablePdfSupport = preferences[ENABLE_PDF_SUPPORT] ?: defaultPreferences.enablePdfSupport,
            language = preferences[LANGUAGE] ?: defaultPreferences.language,
            appTheme = AppTheme.valueOf(preferences[APP_THEME] ?: defaultPreferences.appTheme.name),
            colorScheme = preferences[COLOR_SCHEME] ?: defaultPreferences.colorScheme,
            homeLayout = Layout.valueOf(preferences[HOME_LAYOUT] ?: defaultPreferences.homeLayout.name),
            gridCount = preferences[GRID_COUNT] ?: defaultPreferences.gridCount,
            showEntries = preferences[SHOW_ENTRIES] ?: defaultPreferences.showEntries,
            showRating = preferences[SHOW_RATING] ?: defaultPreferences.showRating,
            showReadingStatus = preferences[SHOW_READING_STATUS] ?: defaultPreferences.showReadingStatus,
            showReadingDates = preferences[SHOW_READING_DATES] ?: defaultPreferences.showReadingDates,
            showPdfLabel = preferences[SHOW_PDF_LABEL] ?: defaultPreferences.showPdfLabel,
            sortBy = SortOption.valueOf(preferences[SORT_BY] ?: defaultPreferences.sortBy.name),
            sortOrder = SortOrder.valueOf(preferences[SORT_ORDER] ?: defaultPreferences.sortOrder.name),
            readingStatus = preferences[READING_STATUS]?.map { ReadingStatus.valueOf(it) }?.toSet() ?: defaultPreferences.readingStatus,
            fileTypes = preferences[FILE_TYPE]?.map { FileType.valueOf(it) }?.toSet() ?: defaultPreferences.fileTypes,
            isPremium = preferences[IS_PREMIUM] ?: defaultPreferences.isPremium
        )
    }


    suspend fun updateAppPreferences(newPreferences: AppPreferences) {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_LAUNCH] = newPreferences.isFirstLaunch
            preferences[SCAN_DIRECTORY] = newPreferences.scanDirectories
            preferences[ENABLE_PDF_SUPPORT] = newPreferences.enablePdfSupport
            preferences[LANGUAGE] = newPreferences.language
            preferences[APP_THEME] = newPreferences.appTheme.name
            preferences[COLOR_SCHEME] = newPreferences.colorScheme
            preferences[HOME_LAYOUT] = newPreferences.homeLayout.name
            preferences[GRID_COUNT] = newPreferences.gridCount
            preferences[SHOW_ENTRIES] = newPreferences.showEntries
            preferences[SHOW_RATING] = newPreferences.showRating
            preferences[SHOW_READING_STATUS] = newPreferences.showReadingStatus
            preferences[SHOW_READING_DATES] = newPreferences.showReadingDates
            preferences[SHOW_PDF_LABEL] = newPreferences.showPdfLabel
            preferences[SORT_BY] = newPreferences.sortBy.name
            preferences[SORT_ORDER] = newPreferences.sortOrder.name
            preferences[READING_STATUS] = newPreferences.readingStatus.map { it.name }.toSet()
            preferences[FILE_TYPE] = newPreferences.fileTypes.map { it.name }.toSet()
            preferences[IS_PREMIUM] = newPreferences.isPremium
        }
    }


    suspend fun resetLayoutPreferences() {
        dataStore.edit { preferences ->
            preferences[HOME_LAYOUT] = defaultPreferences.homeLayout.name
            preferences[GRID_COUNT] = defaultPreferences.gridCount
            preferences[SHOW_ENTRIES] = defaultPreferences.showEntries
            preferences[SHOW_RATING] = defaultPreferences.showRating
            preferences[SHOW_READING_STATUS] = defaultPreferences.showReadingStatus
            preferences[SHOW_READING_DATES] = defaultPreferences.showReadingDates
            preferences[SHOW_PDF_LABEL] = defaultPreferences.showPdfLabel
        }
    }

    suspend fun resetSortPreferences() {
        dataStore.edit { preferences ->
            preferences[SORT_ORDER] = defaultPreferences.sortOrder.name
            preferences[SORT_BY] = defaultPreferences.sortBy.name
        }
    }


     suspend fun updateIsPremium(isPremium: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_PREMIUM] = isPremium
        }

    }


}



