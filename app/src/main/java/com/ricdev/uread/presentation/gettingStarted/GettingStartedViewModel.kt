package com.ricdev.uread.presentation.gettingStarted

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.source.local.AppPreferencesUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GettingStartedViewModel @Inject constructor(
    private val appPreferencesUtil: AppPreferencesUtil,
    application: Application,
) : AndroidViewModel(application) {

    private val _appPreferences = MutableStateFlow(AppPreferencesUtil.defaultPreferences)
    val appPreferences: StateFlow<AppPreferences> = _appPreferences.asStateFlow()

    private val _isButtonsEnabled = MutableStateFlow(true)
    val isButtonsEnabled: StateFlow<Boolean> = _isButtonsEnabled.asStateFlow()


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
            _isButtonsEnabled.value = false
            appPreferencesUtil.updateAppPreferences(newPreferences)
            _appPreferences.value = newPreferences
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun checkForPermission(onPermissionNeeded: () -> Unit) {
        if (Environment.isExternalStorageManager()) {
            updateAppPreferences(
                _appPreferences.value.copy(
                    isFirstLaunch = false,
                )
            )
        } else {
            onPermissionNeeded()
        }
    }


    fun handleStoragePermissionResult(isGranted: Boolean) {
        if (isGranted) {
            updateAppPreferences(
                _appPreferences.value.copy(
                    isFirstLaunch = false,
                )
            )
        } else {
            _isButtonsEnabled.value = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun handleStoragePermissionResult(launcher: ActivityResultLauncher<Intent>) {
        _isButtonsEnabled.value = false
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.fromParts("package", getApplication<Application>().packageName, null)
            }
            launcher.launch(intent)
        } catch (e: Exception) {
            launcher.launch(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
        }
    }



    fun updateEnabledButton(isEnabled: Boolean) {
        _isButtonsEnabled.value = isEnabled
    }
}




//    fun handleStoragePermissionResult(launcher: ActivityResultLauncher<Intent>, asked: Boolean) {
//        if (asked) {
//            _isButtonsEnabled.value = false
//            try {
//                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//                val uri = Uri.fromParts("package", application.packageName, null)
//                intent.data = uri
//                launcher.launch(intent)
//            } catch (e: Exception) {
//                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
//                launcher.launch(intent)
//            }
//        } else {
//            _isButtonsEnabled.value = true
//        }
//    }