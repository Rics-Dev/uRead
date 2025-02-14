package com.ricdev.uread.presentation.sharedComponents

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ricdev.uread.R
import com.ricdev.uread.util.PurchaseHelper
import com.ricdev.uread.navigation.Screens
import com.ricdev.uread.navigation.navigateToScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CustomNavigationDrawer(
    purchaseHelper: PurchaseHelper,
    drawerState: DrawerState,
    navController: NavHostController,
    viewModel: CustomNavigationViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    val appPreferences by viewModel.appPreferences.collectAsStateWithLifecycle()
    val showPremiumScreen by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(if (isPortrait) 24.dp else 0.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (appPreferences.isPremium) {
                        Image(
                            painter = painterResource(id = R.drawable.crown),
                            contentDescription = "Crown",
                            modifier = Modifier
                                .size(if (isPortrait) 24.dp else 16.dp)
                                .offset(y = (if (isPortrait) 36 else 18).dp)
                        )
                    } else {
                        FilledTonalButton(
                            contentPadding = PaddingValues(8.dp),
                            onClick = {
                                navController.navigate(Screens.PremiumScreen.route)
                            },
                            modifier = Modifier
                                .offset(y = (if (isPortrait) 36 else 18).dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.crown),
                                    contentDescription = "Crown",
                                    modifier = Modifier.size(if (isPortrait) 16.dp else 8.dp)
                                )
                                Text(stringResource(R.string.unlock_premium), fontSize = 12.sp)
                                Image(
                                    painter = painterResource(id = R.drawable.crown),
                                    contentDescription = "Crown",
                                    modifier = Modifier.size(if (isPortrait) 16.dp else 8.dp)
                                )
                            }
                        }
                    }
                    Image(
                        painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(if (isPortrait) 150.dp else 100.dp)
                    )
                }
                HorizontalDivider()
                Spacer(Modifier.height(if (isPortrait) 16.dp else 0.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = (if (isPortrait) 0 else 16).dp)
                ) {
                    item {
                        NavigationItem(
                            icon = Icons.Outlined.Home,
                            label = stringResource(R.string.home),
                            isSelected = currentRoute == Screens.HomeScreen.route,
                            onClick = {
                                navigateIfNeeded(
                                    navController,
                                    currentRoute,
                                    drawerState,
                                    Screens.HomeScreen.route,
                                    scope
                                )
                            }
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    item {
                        NavigationItem(
                            icon = Icons.Outlined.QueryStats,
                            label = stringResource(R.string.statistics),
                            isSelected = currentRoute == Screens.StatisticsScreen.route,
                            onClick = {
                                navigateIfNeeded(
                                    navController,
                                    currentRoute,
                                    drawerState,
                                    Screens.StatisticsScreen.route,
                                    scope
                                )
                            }
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    item {
                        NavigationItem(
                            icon = Icons.Outlined.FolderCopy,
                            label = stringResource(R.string.shelves),
                            isSelected = currentRoute == Screens.ShelvesScreen.route,
                            onClick = {
                                navigateIfNeeded(
                                    navController,
                                    currentRoute,
                                    drawerState,
                                    Screens.ShelvesScreen.route,
                                    scope
                                )
                            }
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    item {
                        NavigationItem(
                            icon = Icons.AutoMirrored.Outlined.StickyNote2,
                            label = stringResource(R.string.notes),
                            isSelected = currentRoute == Screens.NotesScreen.route,
                            onClick = {
                                navigateIfNeeded(
                                    navController,
                                    currentRoute,
                                    drawerState,
                                    Screens.NotesScreen.route,
                                    scope
                                )
                            }
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    item {
                        NavigationItem(
                            icon = Icons.Outlined.BorderColor,
                            label = stringResource(R.string.highlights_underlines),
                            isSelected = currentRoute == Screens.AnnotationsScreen.route,
                            onClick = {
                                navigateIfNeeded(
                                    navController,
                                    currentRoute,
                                    drawerState,
                                    Screens.AnnotationsScreen.route,
                                    scope
                                )
                            }
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    item {
                        NavigationItem(
                            icon = Icons.Outlined.Settings,
                            label = stringResource(R.string.settings),
                            isSelected = currentRoute == Screens.SettingsScreen.route,
                            onClick = {
                                navigateIfNeeded(
                                    navController,
                                    currentRoute,
                                    drawerState,
                                    Screens.SettingsScreen.route,
                                    scope
                                )
                            }
                        )
                    }
                }
            }
        }
    ) {
        content()
    }

   if (showPremiumScreen){
      PremiumScreen(
          navController = navController,
          purchaseHelper = purchaseHelper,
      )
   }

}

@Composable
private fun NavigationItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
        selected = false,
        onClick = onClick,
        badge = {
            if (isSelected) {
                Badge(
                    modifier = Modifier.size(12.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    content = {}
                )
            }
        }
    )
}


private fun navigateIfNeeded(
    navController: NavHostController,
    currentRoute: String?,
    drawerState: DrawerState,
    route: String,
    scope: CoroutineScope,
) {
    if (currentRoute != route) {
        navController.navigateToScreen(route)
    }
    scope.launch {
        drawerState.close()
    }
}