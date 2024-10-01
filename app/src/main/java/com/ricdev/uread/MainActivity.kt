package com.ricdev.uread

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.ads.MobileAds
import com.ricdev.uread.ui.theme.UReadTheme
import com.ricdev.uread.navigation.AppNavigation
import com.ricdev.uread.util.PurchaseHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        splashScreen.setKeepOnScreenCondition { !viewModel.isReady.value }


        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            MobileAds.initialize(this@MainActivity)
        }


        val purchaseHelper = PurchaseHelper(this)
        purchaseHelper.billingSetup()




        setContent {
            val appPreferences by viewModel.appPreferences.collectAsStateWithLifecycle()
            val isReady by viewModel.isReady.collectAsStateWithLifecycle()


            UReadTheme(appPreferences = appPreferences) {
                AppNavigation(
                    appPreferences = appPreferences,
                    isReady = isReady,
                    purchaseHelper = purchaseHelper,
                )
            }
        }
    }

}


