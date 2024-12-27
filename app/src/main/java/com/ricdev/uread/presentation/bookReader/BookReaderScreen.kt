package com.ricdev.uread.presentation.bookReader

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.Rect
import android.graphics.RectF
import android.util.Base64
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ricdev.uread.BuildConfig
import com.ricdev.uread.data.model.AnnotationType
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.BookAnnotation
import com.ricdev.uread.data.model.Note
import com.ricdev.uread.data.model.ReaderPreferences
import com.ricdev.uread.domain.model.DecorationStyleAnnotationMark
import com.ricdev.uread.navigation.Screens
import com.ricdev.uread.presentation.bookReader.components.TextToolbar
import com.ricdev.uread.presentation.bookReader.components.TtsPlayer
import com.ricdev.uread.presentation.bookReader.components.dialogs.NoteContent
import com.ricdev.uread.presentation.bookReader.components.drawers.AnnotationsDrawer
import com.ricdev.uread.presentation.bookReader.components.toolbars.BottomToolbar
import com.ricdev.uread.presentation.bookReader.components.toolbars.TopToolbar
import com.ricdev.uread.presentation.bookReader.components.drawers.ChaptersDrawer
import com.ricdev.uread.presentation.bookReader.components.modals.FontSettings
import com.ricdev.uread.presentation.bookReader.components.drawers.NotesDrawer
import com.ricdev.uread.presentation.bookReader.components.modals.PageSettings
import com.ricdev.uread.presentation.bookReader.components.modals.ReaderSettings
import com.ricdev.uread.presentation.bookReader.components.modals.UiSettings
import com.ricdev.uread.presentation.bookReader.components.dialogs.NoteDialog
import com.ricdev.uread.presentation.bookReader.components.drawers.BookmarksDrawer
import com.ricdev.uread.presentation.bookReader.util.SelectionActionModeCallback
import com.ricdev.uread.util.KeepScreenOn
import com.ricdev.uread.util.PurchaseHelper
import com.ricdev.uread.util.SetFullScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.readium.r2.navigator.DecorableNavigator
import org.readium.r2.navigator.Decoration
import org.readium.r2.navigator.OverflowableNavigator
import org.readium.r2.navigator.VisualNavigator
import org.readium.r2.navigator.epub.EpubDefaults
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.navigator.html.HtmlDecorationTemplate
import org.readium.r2.navigator.html.toCss
import org.readium.r2.navigator.input.DragEvent
import org.readium.r2.navigator.input.InputListener
import org.readium.r2.navigator.input.TapEvent
import org.readium.r2.navigator.util.DirectionalNavigationAdapter
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.services.locateProgression
import org.readium.r2.navigator.html.HtmlDecorationTemplates
import org.readium.r2.shared.DelicateReadiumApi

@OptIn(ExperimentalReadiumApi::class)
@Composable
fun BookReaderScreen(
    navController: NavHostController,
    purchaseHelper: PurchaseHelper,
    viewModel: BookReaderViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val initialLocator by viewModel.initialLocator.collectAsStateWithLifecycle()
    val readerPreferences by viewModel.readerPreferences.collectAsStateWithLifecycle()
    val epubPreferences by viewModel.epubPreferences.collectAsStateWithLifecycle()

    val book by viewModel.book.collectAsStateWithLifecycle()


    // Add a state to control the visibility of the EpubReaderView
    var showReader by remember { mutableStateOf(false) }
    var coverAlpha by remember { mutableFloatStateOf(1f) }
    var readerAlpha by remember { mutableFloatStateOf(0f) }

    val appPreferences by viewModel.appPreferences.collectAsStateWithLifecycle()

    var areToolbarsVisible by remember { mutableStateOf(false) }


    KeepScreenOn(readerPreferences.keepScreenOn)

    LaunchedEffect(uiState) {
        viewModel.fetchInitialLocator()
        if (uiState is BookReaderUiState.Success) {
            delay(2000)
            showReader = true
            // Animate the transition
            animate(1f, 0f, animationSpec = tween(durationMillis = 500)) { value, _ ->
                coverAlpha = value
            }
            animate(0f, 1f, animationSpec = tween(durationMillis = 500)) { value, _ ->
                readerAlpha = value
            }
        }
    }




    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetReadingSession()
        }
    }





    SetFullScreen(context, showSystemBars = areToolbarsVisible)



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is BookReaderUiState.Loading, is BookReaderUiState.Success -> {
                // Book cover
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(coverAlpha),
                    contentAlignment = Alignment.Center
                ) {
                    val request = ImageRequest.Builder(LocalContext.current)
                        .data(book?.coverPath)
                        .size(300)
                        .scale(Scale.FIT)
                        .build()
                    AsyncImage(
                        model = request,
                        contentDescription = "Book cover",
                        modifier = Modifier
                            .fillMaxSize(0.7f)
                            .padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                // Epub reader
                if (showReader && uiState is BookReaderUiState.Success) {
                    val successState = uiState as BookReaderUiState.Success
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(readerAlpha)
                    ) {
                        EpubReaderView(
                            book = book,
                            purchaseHelper = purchaseHelper,
                            navController = navController,
                            publication = successState.publication,
                            onLocatorChange = { locator ->
                                viewModel.updateCurrentLocator(locator)
                            },
                            initialLocator = initialLocator,
                            readerPreferences = readerPreferences,
                            epubPreferences = epubPreferences,
                            viewModel = viewModel,
                            appPreferences = appPreferences,
                            areToolbarsVisible = areToolbarsVisible,
                            onToolbarsVisibilityChanged = {
                                areToolbarsVisible = !areToolbarsVisible
                            }
                        )
                    }
                }
            }

            is BookReaderUiState.Error -> Text((uiState as BookReaderUiState.Error).message)
        }
    }
}


@OptIn(ExperimentalReadiumApi::class, DelicateReadiumApi::class)
@Composable
fun EpubReaderView(
    book: Book?,
    purchaseHelper: PurchaseHelper,
    navController: NavHostController,
    publication: Publication,
    initialLocator: Locator?,
    onLocatorChange: (Locator) -> Unit,
    readerPreferences: ReaderPreferences,
    epubPreferences: EpubPreferences,
    viewModel: BookReaderViewModel,
    appPreferences: AppPreferences,
    areToolbarsVisible: Boolean,
    onToolbarsVisibilityChanged: () -> Unit,
) {

    val context = LocalContext.current
    val fragmentActivity = LocalContext.current as FragmentActivity
    var navigatorFragment by remember { mutableStateOf<EpubNavigatorFragment?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val screenConfiguration = LocalConfiguration.current


    val annotations by viewModel.annotations.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()

    var showReaderSettings by remember { mutableStateOf(false) }
    var showFontSettings by remember { mutableStateOf(false) }
    var showPageSettings by remember { mutableStateOf(false) }
    var showUISettings by remember { mutableStateOf(false) }
    var isChaptersDrawerOpen by remember { mutableStateOf(false) }
    var isNotesDrawerOpen by remember { mutableStateOf(false) }
    var isBookmarksDrawerOpen by remember { mutableStateOf(false) }
    var isHighlightsDrawerOpen by remember { mutableStateOf(false) }
    var showNoteDialog by remember { mutableStateOf(false) }

    var progression by remember { mutableDoubleStateOf(1.0) }
    var currentChapter by remember { mutableStateOf("") }

    var currentDirectionalNavigationAdapter by remember {
        mutableStateOf<DirectionalNavigationAdapter?>(null)
    }

    var currentLocator by remember { mutableStateOf<Locator?>(null) }

    var noteDialogSelectedText by remember { mutableStateOf("") }

    val selectedNote by viewModel.selectedNote.collectAsStateWithLifecycle()

    val selectedAnnotation by viewModel.selectedAnnotation.collectAsStateWithLifecycle()


    var mInterstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }



    var showTextToolbar by remember { mutableStateOf(false) }
    var showColorSelectionPanel by remember { mutableStateOf(false) }
    var textToolbarRect by remember { mutableStateOf<Rect?>(null) }
    val getSelectionPosition: suspend () -> RectF? = {
        navigatorFragment?.currentSelection()?.rect
    }
    var actionSelectedText by remember { mutableStateOf<String?>(null) }
    val getSelectedText: suspend () -> String? = {
        navigatorFragment?.currentSelection()?.locator?.text?.highlight
    }

    val fullScreenAdUnit = BuildConfig.FULL_SCREEN_BOOK_READER_AD_UNIT

    fun loadInterstitialAd(context: Context) {
        if (!appPreferences.isPremium) {
            InterstitialAd.load(
                context,
                fullScreenAdUnit,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        mInterstitialAd = interstitialAd
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        mInterstitialAd = null
                    }
                }
            )
        }
    }

    fun showInterstitialAd(activity: FragmentActivity) {
        if (!appPreferences.isPremium) {
            mInterstitialAd?.let { ad ->
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        mInterstitialAd = null
                        loadInterstitialAd(activity)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        mInterstitialAd = null
                    }
                }
                ad.show(activity)
            }
        }
    }


    fun onPageChange(newPage: Double) {
        navigatorFragment?.let { navigator ->
            coroutineScope.launch {
                val locator = publication.locateProgression(newPage)
                locator?.let {
                    navigator.go(locator)
                }
            }
        }
    }


    fun onChapterChange(locator: Locator) {
        coroutineScope.launch {
            // Show the ad before navigating to the new chapter
            if (!appPreferences.isPremium) {
                showInterstitialAd(fragmentActivity)
            }
            navigatorFragment?.go(locator)
        }
    }


    fun handleAnnotation(type: AnnotationType, color: Color) {
        coroutineScope.launch {
            val selection = navigatorFragment?.currentSelection()
            val text = selection?.locator?.text?.highlight
            if (selection != null && text != null) {
                val locator = selection.locator
                val bookId = viewModel.currentBookId.value ?: return@launch

                // Get the latest annotations from the ViewModel
                val latestAnnotations = viewModel.getLatestAnnotations()

                // Check if an annotation already exists for this selection
                val existingAnnotation = latestAnnotations.find { annotation ->
                    val annotationLocator = Locator.fromJSON(JSONObject(annotation.locator))
                    annotationLocator?.href == locator.href &&
                            annotationLocator.locations.progression == locator.locations.progression &&
                            annotationLocator.text.highlight == text &&
                            annotation.type == type
                }

                if (existingAnnotation != null) {
                    // Update the existing annotation
                    val updatedAnnotation = existingAnnotation.copy(
                        color = color.toArgb().toString()
                    )
                    viewModel.updateAnnotation(updatedAnnotation)
                } else {
                    // Create a new annotation
                    val newAnnotation = BookAnnotation(
                        bookId = bookId,
                        locator = locator.toJSON().toString(),
                        color = color.toArgb().toString(),
                        note = text,
                        type = type
                    )
                    viewModel.addAnnotation(newAnnotation)
                }
            }
        }
    }


    // Handle text highlight
    fun handleHighlight(color: Color) {
        handleAnnotation(AnnotationType.HIGHLIGHT, color)
    }

    fun handleUnderline(color: Color) {
        handleAnnotation(AnnotationType.UNDERLINE, color)
    }

    fun handleNote() {
        coroutineScope.launch {
            val selection = navigatorFragment?.currentSelection()
            if (selection != null) {
                val selectedText = selection.locator.text.highlight.toString()
                showNoteDialog = true
                noteDialogSelectedText = selectedText
            }
        }
    }

    fun encodeSvgToBase64(assetManager: AssetManager, fileName: String): String {
        val inputStream = assetManager.open(fileName)
        val bytes = inputStream.readBytes()
        inputStream.close()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }


    fun noteTemplate(
        @ColorInt defaultTint: Int = Color.Yellow.toArgb(),
        assetManager: AssetManager
    ): HtmlDecorationTemplate {
        val className = "testapp-annotation-mark"
        val svgBase64 = encodeSvgToBase64(assetManager, "annotation-icon.svg")
        val iconUrl = "data:image/svg+xml;base64,$svgBase64"


        return HtmlDecorationTemplate(
            layout = HtmlDecorationTemplate.Layout.BOUNDS,
//            width = HtmlDecorationTemplate.Width.WRAP,
            width = HtmlDecorationTemplate.Width.PAGE,
            element = { decoration ->
                val style = decoration.style as? DecorationStyleAnnotationMark
                val tint = style?.tint ?: defaultTint
                """
            <div class="note-container">
                <div class="note-highlight" style="background-color: ${tint.toCss()} !important; opacity: 0.2;"></div>
                <div data-activable="1" class="$className" style="background-color: ${tint.toCss()} !important"></div>
            </div>
            """
            },
            stylesheet = """
            .note-container {
                position: relative;
            }
            .note-highlight {
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                z-index: 1;
            }
            .$className {
                position: relative;
                float: left;
                margin-top: -18px;
                margin-left: 0px;
                width: 30px;
                height: 30px;
                border-radius: 50%;
                background: url('$iconUrl') no-repeat center;
                background-size: 65%;
                opacity: 0.8;
                z-index: 2;
            }
            """
        )
    }

    fun highlightTemplate(@ColorInt defaultTint: Int = Color.Yellow.toArgb()): HtmlDecorationTemplate {
        return HtmlDecorationTemplate(
            layout = HtmlDecorationTemplate.Layout.BOXES,
            element = { decoration ->
                val style = decoration.style as? Decoration.Style.Highlight
                val tint = style?.tint ?: defaultTint
                """<div style="background-color: ${tint.toCss()} !important; opacity: 0.3;"></div>"""
            }
        )
    }

    fun underlineTemplate(@ColorInt defaultTint: Int = Color.Yellow.toArgb()): HtmlDecorationTemplate {
        return HtmlDecorationTemplate(
            layout = HtmlDecorationTemplate.Layout.BOXES,
            element = { decoration ->
                val style = decoration.style as? Decoration.Style.Underline
                val tint = style?.tint ?: defaultTint
                """<span style="border-bottom: 2px solid ${tint.toCss()} !important;"></span>"""
            }
        )
    }


    val decorationListener = object : DecorableNavigator.Listener {
        override fun onDecorationActivated(event: DecorableNavigator.OnActivatedEvent): Boolean {

            val tapPosition = event.point?.let { point ->
                Rect(
                    point.x.toInt(),
                    point.y.toInt() + 100,
                    point.x.toInt(),
                    point.y.toInt()
                )
            }

            val decorationId = event.decoration.id
            if (decorationId.startsWith("note-")) {
                val noteId = decorationId.removePrefix("note-").toLongOrNull() ?: return false
                viewModel.selectNote(noteId)
                return true
            }


            val annotationId = event.decoration.id.toLongOrNull() ?: return false
            val annotation = annotations.find { it.id == annotationId } ?: return false


            when (annotation.type) {
                AnnotationType.HIGHLIGHT, AnnotationType.UNDERLINE -> {
                    viewModel.selectAnnotation(annotation)
                    textToolbarRect = tapPosition
                    showTextToolbar = true
                    showColorSelectionPanel = true
                }
            }
            return true
        }
    }


    LaunchedEffect(Unit) {
        if (!appPreferences.isPremium) {
            loadInterstitialAd(context)
        }
//        viewModel.initializeTtsNavigator(navigatorFragment, context, initialLocator)
    }

    // Set up the selection listener
    LaunchedEffect(navigatorFragment) {
        val screenWidth = screenConfiguration.screenWidthDp.toFloat()
        val screenHeight = screenConfiguration.screenHeightDp.toFloat()


        // Convert dp to pixels
        val screenWidthPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            screenWidth,
            Resources.getSystem().displayMetrics
        )
        val screenHeightPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            screenHeight,
            Resources.getSystem().displayMetrics
        )

        navigatorFragment?.let { navigator ->
            (navigator as? VisualNavigator)?.apply {
                addInputListener(object : InputListener {
                    override fun onTap(event: TapEvent): Boolean {

                        val horizontalThirdPx = screenWidthPx / 4f
                        val verticalThirdPx = screenHeightPx / 5f

                        if (event.point.x in horizontalThirdPx..(3f * horizontalThirdPx) &&
                            event.point.y in verticalThirdPx..(4f * verticalThirdPx)
                        ) {
                            onToolbarsVisibilityChanged()
                            return true
                        }


                        return false
                    }

                    override fun onDrag(event: DragEvent): Boolean {
                        // Handle drag events if needed
                        return false
                    }
                })
            }
        }
    }


    // Set up locator and progression
    LaunchedEffect(navigatorFragment) {
        navigatorFragment?.let { navigator ->
            navigator.currentLocator.collect { locator ->
                onLocatorChange(locator)
                progression = locator.locations.totalProgression ?: 0.0
                currentChapter = locator.title ?: ""
                currentLocator = locator
            }
        }
    }



    LaunchedEffect(annotations, notes, navigatorFragment) {
        navigatorFragment?.let { navigator ->
            if (navigator.isAdded) {
                // Remove existing decorations and listener
                navigator.applyDecorations(emptyList(), "user-annotations")
                navigator.removeDecorationListener(decorationListener)

                // Apply new decorations
                val allDecorations = annotations.map { annotation ->
                    Decoration(
                        id = annotation.id.toString(),
                        locator = Locator.fromJSON(JSONObject(annotation.locator))!!,
                        style = when (annotation.type) {
                            AnnotationType.HIGHLIGHT -> Decoration.Style.Highlight(
                                tint = annotation.color.toIntOrNull() ?: Color.Yellow.toArgb()
                            )

                            AnnotationType.UNDERLINE -> Decoration.Style.Underline(
                                tint = annotation.color.toIntOrNull() ?: Color.Yellow.toArgb()
                            )
                        }
                    )
                } + notes.map { note ->
                    Decoration(
                        id = "note-${note.id}",
                        locator = Locator.fromJSON(JSONObject(note.locator))!!,
                        style = DecorationStyleAnnotationMark(
                            tint = note.color.toIntOrNull() ?: Color.Yellow.toArgb()
                        ),
                        extras = mapOf("template" to "note")
                    )
                }

                navigator.applyDecorations(allDecorations, "user-annotations")

                // Add the listener after applying decorations
                navigator.addDecorationListener("user-annotations", decorationListener)
            }
        }
    }


    // set up tap navigation
    LaunchedEffect(readerPreferences.tapNavigation) {
        navigatorFragment?.let { navigator ->
            if (navigator.isAdded) {
                (navigator as? OverflowableNavigator)?.let { overflowableNavigator ->
                    if (readerPreferences.tapNavigation) {
                        currentDirectionalNavigationAdapter?.let { adapter ->
                            overflowableNavigator.removeInputListener(adapter)
                        }
                        val newAdapter = DirectionalNavigationAdapter(overflowableNavigator)
                        overflowableNavigator.addInputListener(newAdapter)
                        currentDirectionalNavigationAdapter = newAdapter
                    } else {
                        currentDirectionalNavigationAdapter?.let { adapter ->
                            overflowableNavigator.removeInputListener(adapter)
                        }
                        currentDirectionalNavigationAdapter = null
                    }
                }
            }
        }
    }








    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = readerPreferences.backgroundColor)
    ) {
        AndroidView(
            factory = { context ->
                FrameLayout(context).apply {
                    id = View.generateViewId()
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 28.dp, top = 48.dp),
            update = { view ->
                if (navigatorFragment == null) {
                    val factory = EpubNavigatorFactory(
                        publication = publication,
                        configuration = EpubNavigatorFactory.Configuration(
                            defaults = EpubDefaults(
                                pageMargins = epubPreferences.pageMargins,
                                fontSize = epubPreferences.fontSize,
                                fontWeight = epubPreferences.fontWeight,
                                scroll = epubPreferences.scroll
                            )
                        )
                    )


                    val fragment = factory.createFragmentFactory(
                        initialLocator = initialLocator,
                        listener = null,
                        configuration = EpubNavigatorFragment.Configuration(
                            shouldApplyInsetsPadding = false,
                            selectionActionModeCallback = SelectionActionModeCallback(
                                showCustomMenu = { rect, selectedText ->
                                    textToolbarRect = rect.apply { offset(0, 150) }
                                    actionSelectedText = selectedText
                                    showTextToolbar = true
                                },
                                hideCustomMenu = {
                                    showTextToolbar = false
                                },
                                getSelectedText = getSelectedText,
                                getSelectionPosition = getSelectionPosition
                            ),
                            decorationTemplates = HtmlDecorationTemplates {
                                set(
                                    DecorationStyleAnnotationMark::class,
                                    noteTemplate(assetManager = context.assets)
                                )
                                set(Decoration.Style.Highlight::class, highlightTemplate())
                                set(Decoration.Style.Underline::class, underlineTemplate())
                            }
                        )
                    ).instantiate(
                        fragmentActivity.classLoader,
                        EpubNavigatorFragment::class.java.name
                    )

                    navigatorFragment = fragment as EpubNavigatorFragment

                    fragmentActivity.supportFragmentManager.beginTransaction()
                        .replace(view.id, fragment)
                        .commitAllowingStateLoss()
                }
                navigatorFragment?.let { navigator ->
                    if (navigator.isAdded) {
                        navigator.submitPreferences(epubPreferences)
                    }
                }
            }
        )


        var isBookmarked by remember { mutableStateOf(false) }
        val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()

        LaunchedEffect(currentLocator, bookmarks) {
            isBookmarked = bookmarks.any { it.locator == currentLocator?.toJSON().toString() }
        }

        val ttsNavigator by viewModel.ttsNavigator.collectAsStateWithLifecycle()
        val isTtsOn by viewModel.isTtsOn.collectAsStateWithLifecycle()
        val isTtsPlaying by viewModel.isTtsPlaying.collectAsStateWithLifecycle()
        val ttsSpeed by viewModel.ttsSpeed.collectAsStateWithLifecycle()
        val ttsPitch by viewModel.ttsPitch.collectAsStateWithLifecycle()
        val ttsLanguage by viewModel.ttsLanguage.collectAsStateWithLifecycle()


        //Bookmark
        AnimatedVisibility(
            visible = isBookmarked,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp)
                .offset(y = (-14).dp)

        ) {
            IconButton(
                onClick = {
                    val deletedBookmark =
                        bookmarks.find { it.locator == currentLocator?.toJSON().toString() }
                    if (deletedBookmark != null) {
                        viewModel.deleteBookmark(deletedBookmark)
                    }
                }
            ) {
                Icon(
                    Icons.Default.Bookmark,
                    contentDescription = "Bookmark",
                    modifier = Modifier
                        .size(32.dp),
                    tint = if (readerPreferences.backgroundColor == Color.White) Color.Black else Color.White
                )
            }
        }



        AnimatedVisibility(
            visible = areToolbarsVisible,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it })
        ) {
            TopToolbar(
                isBookmarked = isBookmarked,
                navController = navController,
                book = book,
                publication,
                currentChapter = currentChapter,
                onChaptersClick = { isChaptersDrawerOpen = true },
                onNotesDrawerToggle = { isNotesDrawerOpen = true },
                onBookmarkDrawerToggle = { isBookmarksDrawerOpen = true },
                onHighlightsDrawerToggle = { isHighlightsDrawerOpen = true },
                bookmark = {
                    currentLocator?.let { locator ->
                        val existingBookmark =
                            bookmarks.find { it.locator == locator.toJSON().toString() }
                        if (existingBookmark != null) {
                            viewModel.deleteBookmark(existingBookmark)
                        } else {
                            viewModel.addBookmark(locator)
                        }
                    }
                },
                textToSpeech = {
                    viewModel.toggleTts(navigatorFragment, context)
                },
                isTtsOn = isTtsOn,
            )
        }


        LaunchedEffect(isTtsOn) {
            if (isTtsOn) {
                onToolbarsVisibilityChanged()
            }
        }




        TtsPlayer(
            areToolbarsVisible = areToolbarsVisible,
            isTtsOn = isTtsOn,
            isTtsPlaying = isTtsPlaying,
            speed = ttsSpeed,
            pitch = ttsPitch,
            language = ttsLanguage,
            onPlay = {
                ttsNavigator?.play()
                viewModel.setTtsPlaying(true)
            },
            onPause = {
                ttsNavigator?.pause()
                viewModel.setTtsPlaying(false)
            },
            onEnd = {
                viewModel.toggleTts(navigatorFragment, context)
            },
            onSpeedChange = { viewModel.setTtsSpeed(it.toDouble()) },
            onPitchChange = { viewModel.setTtsPitch(it.toDouble()) },
            onLanguageChange = { viewModel.setTtsLanguage(it) },
            onSkipToNextUtterance = { viewModel.skipToNextUtterance() },
            onSkipToPreviousUtterance = { viewModel.skipToPreviousUtterance() }
        )


        // ActionModeLayout
        if (showTextToolbar || isHighlightsDrawerOpen || isChaptersDrawerOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isNotesDrawerOpen = false
                        isBookmarksDrawerOpen = false
                        isHighlightsDrawerOpen = false
                        isChaptersDrawerOpen = false
                        showTextToolbar = false
                        showColorSelectionPanel = false
                    }
            )
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            AnimatedVisibility(
                visible = !showUISettings && !showFontSettings && !showPageSettings && !showReaderSettings,
            ) {
                BottomToolbar(
                    navigatorFragment = navigatorFragment,
                    showToolbar = areToolbarsVisible,
                    progression = progression,
                    onPageChange = ::onPageChange,
                    onToggleFontSettings = { showFontSettings = true },
                    onTogglePageSettings = { showPageSettings = true },
                    onToggleReaderSettings = { showReaderSettings = true },
                    onToggleUISettings = { showUISettings = true }
                )
            }
        }









        ChaptersDrawer(
            isOpen = isChaptersDrawerOpen,
            currentChapter = currentChapter,
            tableOfContents = publication.tableOfContents,
            onChapterSelect = { selectedChapter ->
                val locator = publication.locatorFromLink(selectedChapter)
                locator?.let {
                    onChapterChange(it)
                    isChaptersDrawerOpen = false
                }
            },
            onClose = { isChaptersDrawerOpen = false }
        )

        NotesDrawer(
            navController = navController,
            viewModel = viewModel,
            purchaseHelper = purchaseHelper,
            appPreferences = appPreferences,
            isOpen = isNotesDrawerOpen,
            onClose = { isNotesDrawerOpen = false },
            notes = notes,
            onNoteClick = { note ->
                // Handle note click, e.g., navigate to the note's location in the book
                coroutineScope.launch {
                    Locator.fromJSON(JSONObject(note.locator))?.let { navigatorFragment?.go(it) }
                    isNotesDrawerOpen = false
                }
            },
            onUpdateNote = { updatedNote ->
                viewModel.updateNote(updatedNote)
            },
            onRemoveNote = { note ->
                viewModel.deleteNote(note)
            }
        )

        BookmarksDrawer(
            navController = navController,
            viewModel = viewModel,
            purchaseHelper = purchaseHelper,
            appPreferences = appPreferences,
            isOpen = isBookmarksDrawerOpen,
            onClose = { isBookmarksDrawerOpen = false },
            bookmarks = bookmarks,
            onBookmarkClick = { bookmark ->
                coroutineScope.launch {
                    Locator.fromJSON(JSONObject(bookmark.locator))
                        ?.let { navigatorFragment?.go(it) }
                    isBookmarksDrawerOpen = false
                }
            },
            onRemoveBookmark = { bookmark ->
                viewModel.deleteBookmark(bookmark)
            }
        )

        AnnotationsDrawer(
            navController = navController,
            viewModel = viewModel,
            purchaseHelper = purchaseHelper,
            appPreferences = appPreferences,
            navigator = navigatorFragment,
            annotations = annotations,
            onRemoveAnnotation = viewModel::deleteAnnotation,
            onUpdateAnnotation = viewModel::updateAnnotation,
            isOpen = isHighlightsDrawerOpen,
            onClose = { isHighlightsDrawerOpen = false }
        )


        if (showNoteDialog) {
            NoteDialog(
                appPreferences = appPreferences,
                selectedText = noteDialogSelectedText,
                onSave = { noteText, selectedColor -> // Capture the selected color
                    coroutineScope.launch {
                        val selection = navigatorFragment?.currentSelection()
                        if (selection != null) {
                            val locator = selection.locator
                            val bookId = viewModel.currentBookId.value ?: return@launch

                            val newNote = Note(
                                locator = locator.toJSON().toString(),
                                selectedText = noteDialogSelectedText,
                                note = noteText,
                                color = selectedColor.toArgb().toString(), // Use selected color
                                bookId = bookId
                            )
                            viewModel.addNote(newNote)
                        }
                    }
                    showNoteDialog = false
                },
                onDismiss = { showNoteDialog = false },
                showPremiumModal = {
                    showNoteDialog = false
                    navController.navigate(Screens.PremiumScreen.route);
//                    viewModel.purchasePremium(purchaseHelper)
//                    showPremiumModal = true
                }
            )
        }


        selectedNote?.let { note ->
            NoteContent(
                appPreferences = appPreferences,
                note = note,
                onDismiss = { viewModel.clearSelectedNote() },
                onEdit = { editedNote ->
                    viewModel.updateNote(editedNote)
                    viewModel.clearSelectedNote()
                },
                onDelete = { noteToDelete ->
                    viewModel.deleteNote(noteToDelete)
                    viewModel.clearSelectedNote()
                },
                showPremiumModal = {
                    viewModel.clearSelectedNote()
//                    viewModel.purchasePremium(purchaseHelper)
                    navController.navigate(Screens.PremiumScreen.route);
                }
            )
        }


        if (showFontSettings) {
            FontSettings(
                viewModel = viewModel,
                readerPreferences = readerPreferences,
                onDismiss = { showFontSettings = false },
            )
        }

        if (showPageSettings) {
            PageSettings(
                viewModel = viewModel,
                readerPreferences = readerPreferences,
                onDismiss = { showPageSettings = false },
            )
        }

        if (showUISettings) {
            UiSettings(
                navController = navController,
                purchaseHelper = purchaseHelper,
                appPreferences = appPreferences,
                viewModel = viewModel,
                readerPreferences = readerPreferences,
                onDismiss = { showUISettings = false }
            )
        }

        if (showReaderSettings) {
            ReaderSettings(
                viewModel = viewModel,
                readerPreferences = readerPreferences,
                onDismiss = { showReaderSettings = false }
            )
        }
    }


    if (showTextToolbar) {
        TextToolbar(
            navController = navController,
            viewModel = viewModel,
            selectedText = actionSelectedText,
            rect = textToolbarRect!!,
            onHighlight = { color ->
                handleHighlight(color)
            },
            onUnderline = { color ->
                handleUnderline(color)
            },
            onNote = {
                handleNote()
                showTextToolbar = false
            },
            onDismiss = { showTextToolbar = false },
            purchaseHelper = purchaseHelper,
            appPreferences = appPreferences,
            selectedAnnotation = selectedAnnotation,
            onRemoveAnnotation = {
                viewModel.deleteAnnotation(it)
            },
            colorHistory = readerPreferences.colorHistory,
            onColorHistoryUpdated = { newHistory ->
                viewModel.updateReaderPreferences(readerPreferences.copy(colorHistory = newHistory))
            },
            showColorSelectionPanel = showColorSelectionPanel
        )
    }




    DisposableEffect(Unit) {
        onDispose {
            navigatorFragment?.let { fragment ->
                fragment.removeDecorationListener(decorationListener)
                fragmentActivity.supportFragmentManager.beginTransaction()
                    .remove(fragment)
                    //.commit()
                    .commitAllowingStateLoss()
            }
        }
    }
}









