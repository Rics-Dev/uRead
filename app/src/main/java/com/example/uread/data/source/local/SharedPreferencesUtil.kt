package com.example.uread.data.source.local

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class SharedPreferencesUtil @Inject constructor(
    private val context: Context
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    // verify if it's the first time the app is launched
    fun isFirstLaunch(): Boolean = sharedPreferences.getBoolean("is_first_launch", true)


    // Save directory from where to load books
    fun saveDirectoryUri(uri: String) {
        sharedPreferences.edit().putString("directory_uri", uri).apply()
    }

    //set first time launch of the app to false
    fun setFirstLaunch(isFirstLaunch: Boolean) {
        sharedPreferences.edit().putBoolean("is_first_launch", isFirstLaunch).apply()
    }


    fun saveLastUpdateTime(context: Context, time: Long) {
        sharedPreferences.edit().putLong("last_update_time", time).apply()
    }



    fun getDirectoryUri(context: Context): String? {
        return sharedPreferences.getString("directory_uri", null)
    }


    fun getLastUpdateTime(context: Context): Long {
        return sharedPreferences.getLong("last_update_time", 0)
    }
}