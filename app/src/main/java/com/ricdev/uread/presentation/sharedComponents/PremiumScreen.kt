package com.ricdev.uread.presentation.sharedComponents

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness1
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.mikepenz.markdown.m3.Markdown
import com.ricdev.uread.R
import com.ricdev.uread.util.PurchaseHelper
import com.ricdev.uread.util.customMarkdownTypography
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    navController: NavHostController,
    purchaseHelper: PurchaseHelper,
    viewModel: PremiumViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    fun readPrivacyPolicy(context: Context): String {
        return try {
            context.assets.open("documentation/PRIVACY_POLICY.md").bufferedReader()
                .use { it.readText() }
        } catch (e: IOException) {
            "Error loading privacy policy"
        }
    }

    val privacyPolicy = remember { readPrivacyPolicy(context) }

    var showPrivacyPolicyModal by remember { mutableStateOf(false) }
    val formattedPrice by purchaseHelper.formattedPrice.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.crown),
                    contentDescription = "Crown",
                    modifier = Modifier.size(80.dp)
                )

                Text(
                    text = stringResource(R.string.premium_version),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.premium_description),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PremiumFeature(stringResource(R.string.feature_ad_free))
                    PremiumFeature(stringResource(R.string.feature_reading_stats))
                    PremiumFeature(stringResource(R.string.feature_app_theme))
                    PremiumFeature(stringResource(R.string.feature_unlimited_shelves))
                    PremiumFeature(stringResource(R.string.feature_highlight_colors))
                    PremiumFeature(stringResource(R.string.feature_support_dev))
                }

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Lifetime",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "One time payment",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column {
                            Text(
                                text = formattedPrice,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.purchasePremium(purchaseHelper) {
                        navController.navigateUp()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Upgrade to Premium")
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
                    text = "Privacy",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable {
                            showPrivacyPolicyModal = true
                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Brightness1, contentDescription = "Dot",  modifier = Modifier.size(8.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
                    text = "Terms of Service",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { /* Navigate to Terms of Service */ }
                )
            }
        }
    }





    if (showPrivacyPolicyModal) {
        ModalBottomSheet(
            shape = BottomSheetDefaults.HiddenShape,
            dragHandle = null,
            onDismissRequest = { showPrivacyPolicyModal = false },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { it != SheetValue.PartiallyExpanded }
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .weight(1f) // Give the scrollable content as much space as possible
                        .verticalScroll(rememberScrollState())
                ) {
                    Markdown(
                        typography = customMarkdownTypography(),
                        content = privacyPolicy
                    )
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://github.com/Rics-Dev/uRead/blob/main/app/src/main/assets/documentation/PRIVACY_POLICY.md")
                                )
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Unable to open Privacy Policy",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Navigate to")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            showPrivacyPolicyModal = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Close")
                    }
                }
            }
        }
    }

}

@Composable
fun PremiumFeature(feature: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = "Check Circle",
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = feature,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}