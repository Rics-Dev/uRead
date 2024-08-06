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

class AppPreferencesUtil @Inject constructor(
    context: Context
) {
    private val dataStore = context.dataStore

    private val isFirstLaunchKey = booleanPreferencesKey("is_first_launch")
    private val booksDirectoryKey = stringPreferencesKey("directory_uri")

    fun isFirstLaunch(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[isFirstLaunchKey] ?: true
        }

    suspend fun saveDirectoryUri(uri: String) {
        dataStore.edit { preferences ->
            preferences[booksDirectoryKey] = uri
        }
    }

    suspend fun setFirstLaunch(isFirstLaunch: Boolean) {
        dataStore.edit { preferences ->
            preferences[isFirstLaunchKey] = isFirstLaunch
        }
    }

    fun getDirectoryUri(): Flow<String?> =
        dataStore.data.map { preferences ->
            preferences[booksDirectoryKey]
        }

    fun getAppState(): Flow<Pair<Boolean, String?>> =
        dataStore.data.map { preferences ->
            Pair(
                preferences[isFirstLaunchKey] ?: true,
                preferences[booksDirectoryKey]
            )
        }
}