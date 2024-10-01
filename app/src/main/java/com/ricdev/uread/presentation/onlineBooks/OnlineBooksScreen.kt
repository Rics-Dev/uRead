package com.ricdev.uread.presentation.onlineBooks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ricdev.uread.R
import com.ricdev.uread.presentation.sharedComponents.CustomNavigationDrawer
import com.ricdev.uread.util.PurchaseHelper
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


data class EbookSite(val name: String, val url: String, val faviconUrl: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineBooksScreen(
    navController: NavHostController,
    purchaseHelper: PurchaseHelper,
) {

//    val context = LocalContext.current


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val ebookSites by remember {
        mutableStateOf(
            listOf(
                EbookSite("Project Gutenberg", "https://www.gutenberg.org/", "https://www.gutenberg.org/favicon.ico"),
                EbookSite("Standard Ebooks", "https://standardebooks.org/", "https://standardebooks.org/favicon.ico"),
            )
        )
    }



    CustomNavigationDrawer(
        purchaseHelper = purchaseHelper,




        drawerState = drawerState,
        navController = navController
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.online_books)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ebookSites) { site ->
                        ListItem(
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                .shadow(
                                    4.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    clip = true
                                )
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(onClick = {
                                    val encodedUrl = URLEncoder.encode(site.url, StandardCharsets.UTF_8.toString())
                                    navController.navigate("webview/$encodedUrl")
                                }),
                            leadingContent = {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(site.faviconUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "${site.name} favicon",
                                    modifier = Modifier.size(32.dp)
                                )
                            },
                            headlineContent = {
                                Text(
                                    site.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}