package com.ricdev.uread.presentation.settings


import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.ricdev.uread.R
import com.ricdev.uread.data.model.AppTheme
import com.ricdev.uread.presentation.sharedComponents.CustomNavigationDrawer
//import com.ricsdev.uread.presentation.sharedComponents.PremiumModal
import com.ricdev.uread.util.PurchaseHelper
import com.ricdev.uread.navigation.Screens
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    purchaseHelper: PurchaseHelper,
    viewModel: SettingsViewModel = hiltViewModel(),
) {

    val context = LocalContext.current
    val reviewManager = remember { ReviewManagerFactory.create(context) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    val appPreferences by viewModel.appPreferences.collectAsStateWithLifecycle()

    val isDarkTheme = when (appPreferences.appTheme) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }


    val elevationOverlay = if (isDarkTheme) {
        Color.White.copy(alpha = 0.09f)
            .compositeOver(MaterialTheme.colorScheme.surface)
    } else {
        MaterialTheme.colorScheme.surface
    }



    CustomNavigationDrawer(
        purchaseHelper = purchaseHelper,
        drawerState = drawerState,
        navController = navController
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    title = { Text(text = stringResource(R.string.settings)) }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
            ) {


                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-12).dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            if (appPreferences.isPremium) {
                                Image(
                                    painter = painterResource(id = R.drawable.crown),
                                    contentDescription = "Crown",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .offset(y = (36).dp)  // Adjust this value to control overlap
                                )
                            }
                            Image(
                                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                                contentDescription = "App Logo",
                                modifier = Modifier.size(150.dp)
                            )

                        }
                    }
                }


                item {

                    ListItem(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = if (!isDarkTheme) {
                                    Color.Black.copy(alpha = 0.8f)
                                } else {
                                    Color.Black.copy(alpha = 0.5f)
                                }
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(elevationOverlay)
                            .clickable(onClick = {
                                navController.navigate(Screens.GeneralSettingsScreen.route)
                            })
                            .fillMaxWidth(),
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.general),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.Tune,
                                contentDescription = "General",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                        )
                    )
                }



                item {
                    ListItem(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = if (!isDarkTheme) {
                                    Color.Black.copy(alpha = 0.8f)
                                } else {
                                    Color.Black.copy(alpha = 0.5f)
                                }
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(elevationOverlay)
                            .clickable(onClick = {
                                navController.navigate(Screens.ThemeScreen.route)
                            })
                            .fillMaxWidth(),
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.theme),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.Palette,
                                contentDescription = "Theme",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                        )
                    )
                }



                item {
                    ListItem(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = if (!isDarkTheme) {
                                    Color.Black.copy(alpha = 0.8f)
                                } else {
                                    Color.Black.copy(alpha = 0.5f)
                                }
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(elevationOverlay)
                            .clickable(onClick = {
                                navController.navigate(Screens.DeletedBooksScreen.route)
                            })
                            .fillMaxWidth(),
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.deleted_books),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = "Trash",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                        )
                    )
                }


                item {
                    ListItem(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = if (!isDarkTheme) {
                                    Color.Black.copy(alpha = 0.8f)
                                } else {
                                    Color.Black.copy(alpha = 0.5f)
                                }
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(elevationOverlay)
                            .clickable(onClick = {
                                navController.navigate(Screens.AboutAppScreen.route + "/${isDarkTheme}")
                            })
                            .fillMaxWidth(),
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.about),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "About",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                        )
                    )
                }


                if (!appPreferences.isPremium) {
                    item {
                        ListItem(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    spotColor = if (!isDarkTheme) {
                                        Color.Black.copy(alpha = 0.8f)
                                    } else {
                                        Color.Black.copy(alpha = 0.5f)
                                    }
                                )
                                .clip(RoundedCornerShape(16.dp))
                                .background(elevationOverlay)
                                .clickable(onClick = {
                                    viewModel.purchasePremium(purchaseHelper)
                                })
                                .fillMaxWidth(),
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.remove_ads),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            leadingContent = {
                                Icon(
                                    Icons.Default.Block,
                                    contentDescription = stringResource(R.string.remove_ads),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent,
                            )
                        )
                    }
                }





                item {
                    ListItem(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = if (!isDarkTheme) {
                                    Color.Black.copy(alpha = 0.8f)
                                } else {
                                    Color.Black.copy(alpha = 0.5f)
                                }
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(elevationOverlay)
                            .clickable(onClick = {
                                val request = reviewManager.requestReviewFlow()
                                request.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // We got the ReviewInfo object
                                        val reviewInfo = task.result
                                        val flow = reviewManager.launchReviewFlow(
                                            context as ComponentActivity,
                                            reviewInfo
                                        )
                                        flow.addOnCompleteListener { _ ->
                                            Log.d("Review", "Review flow completed")
                                            // The flow has finished. The API does not indicate whether the user
                                            // reviewed or not, or even whether the review dialog was shown. Thus, no
                                            // matter the result, we continue our app flow.
                                        }
                                    } else {
                                        // There was some problem, log or handle the error code.
                                        @ReviewErrorCode val reviewErrorCode =
                                            (task.exception as ReviewException).errorCode
                                        Log.e("Review", "Error code: $reviewErrorCode")
                                    }
                                }
                            })
                            .fillMaxWidth(),
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.rate_the_app),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingContent = {
                            Icon(
                                Icons.Outlined.StarRate,
                                contentDescription = "Rating",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                        )
                    )
                }
            }
        }
    }
}