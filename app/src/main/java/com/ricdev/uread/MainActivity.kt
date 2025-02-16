package com.ricdev.uread

import android.content.Context
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
import com.ricdev.uread.data.model.AppLanguage
import com.ricdev.uread.data.source.local.AppPreferencesUtil
import com.ricdev.uread.ui.theme.UReadTheme
import com.ricdev.uread.navigation.SetupNavGraph
import com.ricdev.uread.util.LanguageHelper
import com.ricdev.uread.util.PurchaseHelper
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel: SplashViewModel by viewModels()

    // experimental
    private val languageHelper = LanguageHelper()

    // experimental
//    override fun attachBaseContext(newBase: Context) {
//        val appLanguage = AppLanguage.fromCode(
//            AppPreferencesUtil.defaultPreferences.language
//        )
//        val context = languageHelper.updateBaseContextLocale(newBase, appLanguage)
//        super.attachBaseContext(context)
//    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()

        val initialLanguage = AppLanguage.fromCode(AppPreferencesUtil.defaultPreferences.language)
        languageHelper.updateBaseContextLocale(this, initialLanguage)

        // Keep splash screen visible until loading is complete
        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }

        // Initialize billing first
        val purchaseHelper = PurchaseHelper(this)
        purchaseHelper.billingSetup()

        // Initialize ads in background
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@MainActivity)
        }



        setContent {
            val screen by viewModel.startDestination.collectAsStateWithLifecycle()


            UReadTheme {
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