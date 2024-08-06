// In DataStoreUtil.kt
package com.example.uread.data.source.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "app_prefs")

class DataStoreUtil @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.dataStore

    private val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    private val BOOKS_DIRECTORY = stringPreferencesKey("directory_uri")

    fun isFirstLaunch(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[IS_FIRST_LAUNCH] ?: true
        }

    suspend fun saveDirectoryUri(uri: String) {
        dataStore.edit { preferences ->
            preferences[BOOKS_DIRECTORY] = uri
        }
    }

    suspend fun setFirstLaunch(isFirstLaunch: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_LAUNCH] = isFirstLaunch
        }
    }

    fun getDirectoryUri(): Flow<String?> =
        dataStore.data.map { preferences ->
            preferences[BOOKS_DIRECTORY]
        }

    fun getAppState(): Flow<Pair<Boolean, String?>> =
        dataStore.data.map { preferences ->
            Pair(
                preferences[IS_FIRST_LAUNCH] ?: true,
                preferences[BOOKS_DIRECTORY]
            )
        }
}