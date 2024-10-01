package com.ricdev.uread.presentation.settings.components

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ricdev.uread.presentation.settings.viewmodels.ThemeViewModel
import com.ricdev.uread.ui.theme.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ricdev.uread.R
import com.ricdev.uread.data.model.AppTheme
//import com.ricsdev.uread.presentation.sharedComponents.PremiumModal
import com.ricdev.uread.util.PurchaseHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeScreen(
    navController: NavController,
    purchaseHelper: PurchaseHelper,
    viewModel: ThemeViewModel = hiltViewModel()
) {
    val appPreferences by viewModel.appPreferences.collectAsStateWithLifecycle()
//    var showPremiumModal by remember { mutableStateOf(false) }

    val isDarkTheme = when (appPreferences.appTheme) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }

    val context = LocalContext.current
    val dynamicColorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else null


    val colorSchemes = listOf(
        "Dynamic" to dynamicColorScheme,
        "Light Default" to LightColorScheme,
        "Dark Default" to DarkColorScheme,
        "Light Grey" to LightGreyScheme,
        "Dark Grey" to DarkGreyScheme,
        "Light Sepia" to LightSepiaScheme,
        "Dark Sepia" to DarkSepiaScheme,
        "Light Parchment" to LightParchmentScheme,
        "Dark Parchment" to DarkParchmentScheme,
        "Light Yellow" to LightYellowScheme,
        "Dark Yellow" to DarkYellowScheme,
        "Light Teal" to LightTealScheme,
        "Dark Teal" to DarkTealScheme,
        "Light Blue" to LightBlueScheme,
        "Dark Blue" to DarkBlueScheme,
        "Light Pink" to LightPinkScheme,
        "Dark Pink" to DarkPinkScheme,
        "Light Purple" to LightPurpleScheme,
        "Dark Purple" to DarkPurpleScheme,
        "Light Red" to LightRedScheme,
        "Dark Red" to DarkRedScheme,
        "Light Green" to LightGreenScheme,
        "Dark Green" to DarkGreenScheme,
        ).filter { (name, _) ->
        when {
            name == "Dynamic" -> true
            isDarkTheme -> name.startsWith("Dark")
            else -> name.startsWith("Light")
        }
    }

    val displayNameMapping = mapOf(
        "Light Default" to "Monochrome",
        "Dark Default" to "Monochrome",
        "Light Grey" to "Twilight",
        "Dark Grey" to "Twilight",
        "Light Sepia" to "Sepia",
        "Dark Sepia" to "Sepia",
        "Light Parchment" to "Parchment",
        "Dark Parchment" to "Parchment",
        "Light Yellow" to "Pastel Yellow",
        "Dark Yellow" to "Pastel Yellow",
        "Light Teal" to "Teal",
        "Dark Teal" to "Teal",
        "Light Purple" to "Violet",
        "Dark Purple" to "Violet",
        "Light Pink" to "Pastel Pink",
        "Dark Pink" to "Pastel Pink",
        "Light Red" to "Crimson Red",
        "Dark Red" to "Crimson Red",
        "Light Green" to "Emerald Green",
        "Dark Green" to "Emerald Green",
        "Light Blue" to "Lavender Blue",
        "Dark Blue" to "Lavender Blue",
    )


    LaunchedEffect(isDarkTheme) {
        val currentScheme = appPreferences.colorScheme
        val newScheme = when {
            currentScheme == "Dynamic" -> currentScheme
            isDarkTheme && currentScheme.startsWith("Light") -> "Dark ${
                currentScheme.split(" ").last()
            }"

            !isDarkTheme && currentScheme.startsWith("Dark") -> "Light ${
                currentScheme.split(" ").last()
            }"

            else -> currentScheme
        }
        if (currentScheme != newScheme) {
            viewModel.updateAppPreferences(appPreferences.copy(colorScheme = newScheme))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.theme)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
        ) {
            Text(stringResource(R.string.theme), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            SegmentedThemeControl(
                selectedTheme = appPreferences.appTheme,
                onThemeSelected = { theme ->
                    viewModel.updateAppPreferences(appPreferences.copy(appTheme = theme))
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(stringResource(R.string.color_scheme), style = MaterialTheme.typography.titleMedium)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(colorSchemes) { (name, scheme) ->
                    val displayName = displayNameMapping[name] ?: name
                    ColorSchemePreviewCard(
                        name = displayName,
                        colorScheme = scheme,
                        isSelected = appPreferences.colorScheme == name,
                        onSelect = {
                            if (name == "Dynamic" || appPreferences.isPremium) {
                                viewModel.updateAppPreferences(appPreferences.copy(colorScheme = name))
                            } else {
                                viewModel.purchasePremium(purchaseHelper)
//                                showPremiumModal = true
                            }
                        },
                        isPremium = !appPreferences.isPremium && name != "Dynamic"
                    )
                }
            }
        }
    }

//    if(showPremiumModal){
//        PremiumModal(
//            purchaseHelper = purchaseHelper,
//            hidePremiumModal = { showPremiumModal = false }
//        )
//    }
}

@Composable
fun ColorSchemePreviewCard(
    name: String,
    colorScheme: ColorScheme?,
    isSelected: Boolean,
    onSelect: () -> Unit,
    isPremium: Boolean
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) colorScheme?.primary
                    ?: MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = MaterialTheme.shapes.medium
            ),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme?.surface ?: MaterialTheme.colorScheme.surface
        ),
        onClick = onSelect,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme?.onSurface ?: MaterialTheme.colorScheme.onSurface
                )
                if (isPremium) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.crown),
                            contentDescription = "Crown",
                            modifier = Modifier
                                .size(16.dp)
                        )
                        Text(
                            text = "Premium",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ColorPreviewRow(colorScheme)
            Spacer(modifier = Modifier.height(16.dp))
            ColorPreviewPalette(colorScheme)
        }
    }
}

@Composable
fun ColorPreviewRow(colorScheme: ColorScheme?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ColorPreviewBox(colorScheme?.primary ?: MaterialTheme.colorScheme.primary, "Primary")
        ColorPreviewBox(colorScheme?.secondary ?: MaterialTheme.colorScheme.secondary, "Secondary")
        ColorPreviewBox(colorScheme?.tertiary ?: MaterialTheme.colorScheme.tertiary, "Tertiary")
    }
}

@Composable
fun ColorPreviewPalette(colorScheme: ColorScheme?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(colorScheme?.primary ?: MaterialTheme.colorScheme.primary)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(colorScheme?.secondary ?: MaterialTheme.colorScheme.secondary)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(colorScheme?.tertiary ?: MaterialTheme.colorScheme.tertiary)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(colorScheme?.background ?: MaterialTheme.colorScheme.background)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(colorScheme?.surface ?: MaterialTheme.colorScheme.surface)
        )
    }
}

@Composable
fun ColorPreviewBox(color: Color, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}


