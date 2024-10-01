package com.ricdev.uread.presentation.audioBookReader

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.ricdev.uread.R
import com.ricdev.uread.data.model.Book
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudiobookReaderScreen(
    navController: NavHostController,
    viewModel: AudiobookReaderViewModel = hiltViewModel()
) {
    val loadingState by viewModel.loadingState.collectAsState()
    val audiobook by viewModel.audiobook.collectAsState()


    var showReader by remember { mutableStateOf(false) }
    var coverAlpha by remember { mutableFloatStateOf(1f) }
    var readerAlpha by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        delay(1000)
        showReader = true
        // Animate the transition
        animate(1f, 0f, animationSpec = tween(durationMillis = 500)) { value, _ ->
            coverAlpha = value
        }
        animate(0f, 1f, animationSpec = tween(durationMillis = 500)) { value, _ ->
            readerAlpha = value
        }
    }

    Scaffold(

        modifier = Modifier.background(Color.Transparent),
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Blurred background cover image
            audiobook?.coverPath?.let { coverPath ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(coverPath)
                        .scale(Scale.FILL)
                        .build(),
                    contentDescription = "Blurred book cover",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.2f),
                    contentScale = ContentScale.Crop
                )
            }

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.background
                            ),
                            startY = 0f,
                            endY = 2100f
                        )
                    )
            )

            // Book cover
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(coverAlpha),
                contentAlignment = Alignment.Center
            ) {
                val request = ImageRequest.Builder(LocalContext.current)
                    .data(audiobook?.coverPath)
                    .size(300)
                    .scale(Scale.FIT)
                    .build()
                AsyncImage(
                    model = request,
                    contentDescription = "Book cover",
                    modifier = Modifier
                        .size(300.dp)
                        .shadow(16.dp, shape = RoundedCornerShape(500.dp), clip = true)
                        .clip(RoundedCornerShape(500.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            // Audiobook reader content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .alpha(readerAlpha)
            ) {
                when (loadingState) {
                    is LoadingState.Loading -> LoadingScreen("Loading audiobook...")
                    is LoadingState.BookLoaded -> LoadingScreen("Initializing audio player...")
                    is LoadingState.InitializingPlayer -> LoadingScreen("Preparing audio...")
                    is LoadingState.Ready -> AudiobookPlayerScreen(audiobook, viewModel)
                    is LoadingState.Error -> ErrorScreen((loadingState as LoadingState.Error).message)
                }
            }
            if (loadingState is LoadingState.Ready)
                SettingsControls(
                    modifier = Modifier.alpha(readerAlpha), viewModel = viewModel
                )
        }
    }
}

@Composable
fun LoadingScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = message)
        }
    }
}

@Composable
fun ErrorScreen(errorMessage: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Error: $errorMessage",
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun AudiobookPlayerScreen(
    audiobook: Book?,
    viewModel: AudiobookReaderViewModel
) {
    var coverAlpha by remember { mutableFloatStateOf(0f) }
    val currentPosition by viewModel.currentTime.collectAsState()
    val totalTime by viewModel.totalTime.collectAsState()

    LaunchedEffect(Unit) {
        animate(0f, 1f, animationSpec = tween(durationMillis = 500)) { value, _ ->
            coverAlpha = value
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        AudiobookInfo(audiobook)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(300.dp)
                .alpha(coverAlpha),
            contentAlignment = Alignment.Center
        ) {
            val request = ImageRequest.Builder(LocalContext.current)
                .data(audiobook?.coverPath)
                .scale(Scale.FIT)
                .build()
            AsyncImage(
                model = request,
                contentDescription = "Book cover",
                modifier = Modifier
                    .size(270.dp)
                    .shadow(16.dp, shape = RoundedCornerShape(500.dp), clip = true)
                    .clip(RoundedCornerShape(500.dp)),
                contentScale = ContentScale.Fit
            )

            CircularSlider(
                modifier = Modifier.size(300.dp),
                progress = currentPosition.toFloat() / totalTime,
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 12f
            )
        }
        PlaybackInfo(viewModel)
        AudioControls(viewModel)
    }

}


@Composable
fun CircularSlider(
    modifier: Modifier = Modifier,
    progress: Float,
    color: Color = Color.Blue,
    strokeWidth: Float = 8f
) {

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2f - strokeWidth / 2f
        val sweepAngle = 360 * progress

        drawCircle(
            color = color.copy(alpha = 0.3f),
            radius = radius,
            center = center,
            style = Stroke(strokeWidth)
        )

        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(strokeWidth)
        )
    }
}

@Composable
fun AudiobookInfo(audiobook: Book?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = audiobook?.title ?: "",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.W500
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = audiobook?.authors ?: "",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.W400
        )
    }
}

@Composable
fun PlaybackInfo(
    viewModel: AudiobookReaderViewModel,
) {
    var showRemainingTime by remember { mutableStateOf(false) }
    val currentPosition by viewModel.currentTime.collectAsState()
    val totalTime by viewModel.totalTime.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val currentTime = currentPosition
        val duration = totalTime.takeIf { it > 0 } ?: 1L
        val timeToShow = if (showRemainingTime) duration - currentTime else currentTime

        Slider(
            value = currentTime.toFloat(),
            onValueChange = { viewModel.seekTo(it.toLong()) },
            valueRange = 0f..duration.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (showRemainingTime) "-${formatTime(timeToShow)}" else formatTime(
                    timeToShow
                ),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.clickable { showRemainingTime = !showRemainingTime }
            )
            Text(
                text = formatTime(duration),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

fun formatTime(timeMs: Long): String {
    val totalSeconds = timeMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
}

@Composable
fun AudioControls(
    viewModel: AudiobookReaderViewModel,
) {
    val isPlaying by viewModel.isPlaying.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ElevatedButton(
                    contentPadding = PaddingValues(0.dp),
                    onClick = { viewModel.skipBackward() }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipPrevious,
                        contentDescription = "Skip backward",
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(text = "-15s", style = MaterialTheme.typography.bodySmall)
            }


            ElevatedButton(
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(50),
                modifier = Modifier.size(68.dp),
                onClick = { viewModel.playPause() }
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = "play / pause",
                    modifier = Modifier.size(46.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ElevatedButton(
                    contentPadding = PaddingValues(0.dp),
                    onClick = { viewModel.skipForward() }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = "Skip forward",
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(text = "+15s", style = MaterialTheme.typography.bodySmall)
            }
        }


    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsControls(
    modifier: Modifier,
    viewModel: AudiobookReaderViewModel,
) {

    var showSettingsModal by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(28.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ElevatedButton(
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(50),
                modifier = Modifier.size(48.dp),
                onClick = {
                    showSettingsModal = true
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "settings",
                    modifier = Modifier.size(28.dp)
                )
            }
        }


        val configuration = LocalConfiguration.current
        val screenWidthDp = configuration.screenWidthDp
        val sheetMaxWidth = screenWidthDp * 0.95f
        val playbackSpeed by viewModel.playbackSpeed.collectAsState()
        val pitch by viewModel.pitch.collectAsState()

        if (showSettingsModal) {
            ModalBottomSheet(
                modifier = Modifier.padding(bottom = 28.dp),
                shape = RoundedCornerShape(16.dp),
                sheetMaxWidth = sheetMaxWidth.dp,
                onDismissRequest = { showSettingsModal = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.speed), style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = playbackSpeed,
                        onValueChange = { viewModel.setPlaybackSpeed(it) },
                        valueRange = 0.25f..1.75f,
                        steps = 3
                    )
                    Text("${playbackSpeed}x", style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(stringResource(R.string.pitch), style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = pitch,
                        onValueChange = { viewModel.setPitch(it) },
                        valueRange = 0.25f..1.75f,
                        steps = 3
                    )
                    Text("${pitch}x", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}