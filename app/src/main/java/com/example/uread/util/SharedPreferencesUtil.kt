package com.example.uread.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesUtil {

    fun isFirstLaunch(context: Context): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("is_first_launch", true)
    }

    fun setFirstLaunch(context: Context, isFirstLaunch: Boolean) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("is_first_launch", isFirstLaunch)
            apply()
        }
    }

    fun saveDirectoryUri(context: Context, uri: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("directory_uri", uri)
            apply()
        }
    }

    fun getDirectoryUri(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("directory_uri", null)
    }


    fun getLastUpdateTime(context: Context): Long {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("last_update_time", 0)
    }

    fun saveLastUpdateTime(context: Context, time: Long) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putLong("last_update_time", time)
            apply()
        }
    }

}