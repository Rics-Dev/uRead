package com.example.uread.presentation.bookReader.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import org.readium.r2.shared.publication.Publication

@Composable
fun TopToolbar(showToolbar: Boolean, publication: Publication ,fragmentActivity: FragmentActivity) {
    AnimatedVisibility(
        visible = showToolbar,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),  // Slide in from top
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
        modifier = Modifier
            .shadow(elevation = 10.dp)
            .background(Color.White.copy(alpha = 1f))
            .fillMaxWidth()
            .height(100.dp)
            .padding(top = 18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back button
            IconButton(onClick = {
                fragmentActivity.onBackPressedDispatcher.onBackPressed()
            }) {
                Icon(Icons.AutoMirrored.Sharp.ArrowBack, contentDescription = "Back")
            }

            // Book Title
            publication.metadata.title?.let {
                Text(
                    maxLines = 1,
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Book Options buttons
            Row {
                IconButton(onClick = { /* TODO: Handle book options */ }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
                IconButton(onClick = { /* TODO: Handle other options */ }) {
                    Icon(Icons.Default.MoreVert , contentDescription = "More Options")
                }
            }
        }
    }
}