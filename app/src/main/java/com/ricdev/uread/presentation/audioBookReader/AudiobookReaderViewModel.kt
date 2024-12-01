package com.ricdev.uread.presentation.audioBookReader

import android.app.Application
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.domain.use_case.books.GetBookByIdUseCase
import com.ricdev.uread.domain.use_case.books.UpdateBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.ErrorException
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.streamer.PublicationOpener
import javax.inject.Inject

@HiltViewModel
class AudiobookReaderViewModel @Inject constructor(
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val updateBookUseCase: UpdateBookUseCase,
    private val assetRetriever: AssetRetriever,
    private val publicationOpener: PublicationOpener,
    savedStateHandle: SavedStateHandle,
    context: Application,
) : AndroidViewModel(context) {
    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Loading)
    val loadingState = _loadingState.asStateFlow()

    private val _audiobook = MutableStateFlow<Book?>(null)
    val audiobook = _audiobook.asStateFlow()


    private val _exoPlayer = MutableStateFlow<ExoPlayer?>(null)
//    val exoPlayer = _exoPlayer.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentChapter = MutableStateFlow(0)
//    val currentChapter = _currentChapter.asStateFlow()

    private val _currentTime = MutableStateFlow(0L)
    val currentTime = _currentTime.asStateFlow()

    private val _totalTime = MutableStateFlow(0L)
    val totalTime = _totalTime.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed = _playbackSpeed.asStateFlow()

    private val _pitch = MutableStateFlow(1.0f)
    val pitch = _pitch.asStateFlow()

    private val handler = Handler(Looper.getMainLooper())
    private val pollIntervalMs: Long = 500

    private val updatePositionRunnable = object : Runnable {
        override fun run() {
            _exoPlayer.value?.let { player ->
                _currentTime.value = player.currentPosition
                _currentChapter.value = player.currentMediaItemIndex
                if (player.isPlaying) {
                    handler.postDelayed(this, pollIntervalMs)
                }
            }
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            updatePlayerState()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            if (isPlaying) {
                handler.postDelayed(updatePositionRunnable, pollIntervalMs)
            } else {
                handler.removeCallbacks(updatePositionRunnable)
            }
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            updatePlayerState()
        }
    }




    private var publication: Publication? = null

    init {
        val audiobookId = savedStateHandle.get<String>("bookId")?.toLongOrNull()
        val audiobookUri = savedStateHandle.get<String>("bookUri")

        viewModelScope.launch {
            try {
                _loadingState.value = LoadingState.Loading
                audiobookId?.let { id ->
                    loadBook(id)
                }
                audiobookUri?.let { uri ->
                    Uri.parse(uri).toString().let {
                        prepareAudiobook(it)
                    }
                }
            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private suspend fun loadBook(id: Long) {
        withContext(Dispatchers.IO) {
            val book = getBookByIdUseCase(id)
            withContext(Dispatchers.Main) {
                _audiobook.value = book
                _loadingState.value = LoadingState.BookLoaded
            }
        }
    }

    private suspend fun prepareAudiobook(audiobookUri: String) {
        withContext(Dispatchers.Main) {
            try {
                _loadingState.value = LoadingState.InitializingPlayer

                val asset = assetRetriever.retrieve(AbsoluteUrl(audiobookUri)!!).getOrElse { throw ErrorException(it) }
                publication = publicationOpener.open(asset, allowUserInteraction = true)
                    .getOrElse { throw ErrorException(it) }

                initializeExoPlayer(audiobookUri)
            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error(e.message ?: "Error initializing ExoPlayer")
            }
        }
    }

    private fun initializeExoPlayer(audiobookUri: String) {
        _exoPlayer.value = ExoPlayer.Builder(getApplication()).build().apply {
            setMediaItem(MediaItem.fromUri(audiobookUri))
            addListener(playerListener)
            prepare()
            _audiobook.value?.let { book ->
                seekTo(book.readingTime)
            }
        }

        _loadingState.value = LoadingState.Ready
        updatePlayerState()
        updatePlaybackParameters()
    }

    private fun updatePlaybackParameters() {
        _exoPlayer.value?.let { player ->
            player.playbackParameters = PlaybackParameters(_playbackSpeed.value, _pitch.value)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        updatePlaybackParameters()
    }

    fun setPitch(pitch: Float) {
        _pitch.value = pitch
        updatePlaybackParameters()
    }

    private fun updatePlayerState() {
        viewModelScope.launch {
            _exoPlayer.value?.let { player ->
                _currentChapter.value = player.currentMediaItemIndex
                _currentTime.value = player.currentPosition
                _totalTime.value = player.duration
            }
        }
    }





    fun playPause() {
        _exoPlayer.value?.let { player ->
            if (player.isPlaying) {
                player.pause()
                saveCurrentPosition()
            } else {
                player.play()
            }
        }
    }

    fun seekTo(position: Long) {
        _exoPlayer.value?.seekTo(position)
        saveCurrentPosition()
    }


    fun skipForward() {
        _exoPlayer.value?.seekForward()
    }

    fun skipBackward() {
        _exoPlayer.value?.seekBack()
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(updatePositionRunnable)
        _exoPlayer.value?.let { player ->
            player.removeListener(playerListener)
            player.release()
        }
        _exoPlayer.value = null
    }


    private fun saveCurrentPosition() {
        viewModelScope.launch(Dispatchers.IO) {
            _audiobook.value?.let { book ->
                val progression = if (_totalTime.value != 0L) {
                    (_currentTime.value.toFloat() * 100f) / _totalTime.value
                } else {
                    0f
                }
                val updatedBook = book.copy(readingTime = _currentTime.value, progression = progression)
                updateBookUseCase(updatedBook)
            }
        }
    }

}

