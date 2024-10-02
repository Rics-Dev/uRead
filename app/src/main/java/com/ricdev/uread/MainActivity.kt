package com.ricdev.uread

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.ricdev.uread.ui.theme.UReadTheme
import com.ricdev.uread.navigation.SetupNavGraph
import com.ricdev.uread.util.PurchaseHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            viewModel.startDestination.value == null
        }

        // Background initialization of MobileAds
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@MainActivity)
        }

        val purchaseHelper = PurchaseHelper(this)
        purchaseHelper.billingSetup()

        setContent {
            val appPreferences by viewModel.appPreferences.collectAsStateWithLifecycle()
            val screen by viewModel.startDestination.collectAsStateWithLifecycle()

            UReadTheme(appPreferences = appPreferences) {
                val navController = rememberNavController()

                screen?.let {
                    SetupNavGraph(
                        navController = navController,
                        startDestination = it,
                        purchaseHelper = purchaseHelper,
                    )
                }
            }
        }
    }
}
