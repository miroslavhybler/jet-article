package mir.oslav.jet.html.composables.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.text.toSpannable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import mir.oslav.jet.annotations.JetBenchmark
import mir.oslav.jet.annotations.JetExperimental
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.JetRippleTheme
import mir.oslav.jet.html.LocalHtmlDimensions
import mir.oslav.jet.html.composables.BrandingFooter
import mir.oslav.jet.html.composables.HtmlConfig
import mir.oslav.jet.html.composables.MaterialColorPallete
import mir.oslav.jet.html.composables.elements.HtmlAddress
import mir.oslav.jet.html.composables.elements.images.HtmlImage
import mir.oslav.jet.html.composables.elements.HtmlInvalid
import mir.oslav.jet.html.composables.elements.HtmlMetrics
import mir.oslav.jet.html.composables.elements.HtmlQuoete
import mir.oslav.jet.html.composables.elements.HtmlTable
import mir.oslav.jet.html.composables.elements.topbars.HtmlCollapsingTopbar
import mir.oslav.jet.html.composables.elements.images.HtmlPhotoGallery
import mir.oslav.jet.html.composables.elements.topbars.HtmlTopBarSimple
import mir.oslav.jet.html.composables.elements.topbars.rememberCollapsingTopBarState
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.data.HtmlHeader
import mir.oslav.jet.html.toAnnotatedString
import mir.oslav.jet.html.toHtml


/**
 * @param modifier
 * @param data
 * @param config
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
@Composable
@JetBenchmark
@JetExperimental
fun JetHtmlArticleScreen(
    modifier: Modifier = Modifier,
    data: HtmlData,
    config: HtmlConfig = HtmlConfig(spanCount = 1),
    navHostController: NavHostController
) {

    val colorScheme = MaterialTheme.colorScheme
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val context = LocalContext.current
    val view = LocalView.current
    val screenHeight = configuration.screenHeightDp.dp


    val jetRippleTheme = remember { JetRippleTheme() }
    var dimensions by remember { mutableStateOf(value = HtmlDimensions.empty) }
    val systemUiController = rememberSystemUiController()
    val gridState = rememberLazyGridState()

    val topBarState = rememberCollapsingTopBarState()

    var elevation by remember { mutableStateOf(value = 0.dp) }
    var topBarHeight by remember { mutableIntStateOf(value = 0) }
    var scrollOffset by remember { mutableStateOf(value = Offset.Zero) }


    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (
                    gridState.firstVisibleItemIndex == 0
                    && gridState.firstVisibleItemScrollOffset == 0
                ) {
                    scrollOffset = Offset.Zero
                } else {
                    scrollOffset += available
                }

                val offsetDp = with(density) { scrollOffset.y.toDp() }

                val consumed = topBarState.collapse(
                    available = available,
                    scrollOffset = scrollOffset
                )

                return if (offsetDp <= dimensions.collapsingTopBar.minHeight) consumed else Offset.Zero
            }
        }
    }


    LaunchedEffect(
        key1 = Unit,
        block = {
            dimensions = HtmlDimensions().also { dimensions ->
                dimensions.init(configuration = configuration)
            }
            systemUiController.setStatusBarColor(color = colorScheme.background, darkIcons = true)
            systemUiController.setNavigationBarColor(color = Color.Transparent, darkIcons = true)

            if (context is Activity) {
                (context as? Activity)?.window?.let { activityWindow ->
                    WindowCompat.setDecorFitsSystemWindows(activityWindow, false)
                    WindowInsetsControllerCompat(activityWindow, view)
                        .show(WindowInsetsCompat.Type.systemBars())
                }
            }
        }
    )

    CompositionLocalProvider(
        LocalRippleTheme provides jetRippleTheme,
        LocalHtmlDimensions provides dimensions
    ) {

        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .nestedScroll(connection = nestedScrollConnection),
            topBar = {
                HtmlTopBar(data = data, navHostController = navHostController, config = config)
            }
        ) { paddingValues ->
            val original = with(density) {
                (topBarHeight.toDp() - dimensions.collapsingTopBar.maxHeight).toPx()
            }

            val topPaddingExtra = with(density) {
                original
                    .coerceAtLeast(minimumValue = 0f)
                    .toDp()
            }

            Log.d(
                "mirek",
                "extra: $topPaddingExtra  " +
                        "original: $original  " +
                        "maxH: ${dimensions.collapsingTopBar.maxHeight}  " +
                        "h: ${with(density) { topBarHeight.toDp() }}"
            )

            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = with(density) { topBarHeight.toDp() }),
                state = gridState,
                columns = GridCells.Fixed(count = config.spanCount),
                contentPadding = paddingValues,
                content = {
                    when (data) {
                        is HtmlData.Empty -> {
                            item(
                                span = { GridItemSpan(currentLineSpan = config.spanCount) }
                            ) {
                                Text(text = "TODO empty")
                            }
                        }

                        is HtmlData.Invalid -> {
                            item(
                                span = { GridItemSpan(currentLineSpan = config.spanCount) }
                            ) {
                                HtmlInvalid(data = data)
                            }
                        }

                        is HtmlData.Success -> {
                            item(
                                span = { GridItemSpan(currentLineSpan = config.spanCount) },
                            ) {
                                HtmlMetrics(
                                    monitoring = data.monitoring,
                                    modifier = Modifier
                                )
                            }

                            item(
                                span = { GridItemSpan(currentLineSpan = config.spanCount) }
                            ) {
                                MaterialColorPallete()
                            }

                            itemsIndexed(
                                span = { index, item -> GridItemSpan(currentLineSpan = item.span) },
                                items = data.htmlElements,
                                contentType = { intex, element -> element },
                                key = { index, element -> index }
                            ) { index, element ->
                                when (element) {
                                    is HtmlElement.Parsed.Image -> HtmlImage(data = element)
                                    is HtmlElement.Parsed.Quote -> HtmlQuoete(data = element)
                                    is HtmlElement.Parsed.Table -> HtmlTable(data = element)
                                    is HtmlElement.Parsed.Address -> HtmlAddress(address = element)
                                    is HtmlElement.Parsed.TextBlock -> {
                                        Text(
                                            text = remember {
                                                element.text.toHtml()
                                                    .toSpannable()
                                                    .toAnnotatedString(primaryColor = colorScheme.primary)
                                            },
                                            modifier = Modifier.padding(horizontal = dimensions.sidePadding)
                                        )
                                    }

                                    // TODO split parsed and constructed
                                    is HtmlElement.Constructed.Gallery -> {
                                        HtmlPhotoGallery(gallery = element)
                                    }

                                    else -> throw IllegalStateException(
                                        "Element ${element.javaClass.simpleName} not supported yet!"
                                    )
                                }
                            }


                            item(
                                span = { GridItemSpan(currentLineSpan = config.spanCount) }
                            ) {
                                BrandingFooter()
                            }
                        }
                    }
                }
            )
        }
    }
}


@Composable
private fun HtmlTopBar(
    modifier: Modifier = Modifier,
    data: HtmlData,
    navHostController: NavHostController,
    config: HtmlConfig,
) {
    when (data) {
        is HtmlData.Success -> {
            when (data.header) {
                is HtmlHeader.TopBarHeader -> {

                    when (config.topBarConfig) {
                        HtmlConfig.TopBarConfig.SIMPLE -> {
                            HtmlTopBarSimple(
                                navHostController = navHostController,
                                title = data.title,
                                shadow = 4.dp,
                                backgroundAlpha = 1f,
                                modifier = modifier
                            )
                        }

                        HtmlConfig.TopBarConfig.NONE -> return
                        else -> throw IllegalStateException("Not implemented yet!")
                        /*
                    HtmlConfig.TopBarConfig.COLLAPSING -> {

        HtmlCollapsingTopbar(
            navHostController = navHostController,
            title = data.title,
            shadow = elevation,
            //    backgroundAlpha = backgroundAlpha,
            scrollOffset = scrollOffset,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .onSizeChanged { size ->
                    topBarHeight = size.height
                },
            state = topBarState
        )

                        }

                         */
                    }
                }

                is HtmlHeader.FullScreenHeader -> {

                }

                HtmlHeader.None -> {}
            }
        }

        else -> {}
    }
}


/*
CompositionLocalProvider(
LocalRippleTheme provides jetRippleTheme,
LocalHtmlDimensions provides dimensions
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            when (data) {
                is HtmlData.Success -> {
                    when (data.header) {
                        is HtmlHeader.TopBarHeader -> {
                            HtmlCollapsingTopbar(
                                navHostController = navHostController,
                                title = data.title,
                                shadow = elevation,
                                //    backgroundAlpha = backgroundAlpha,
                                scrollOffset = scrollOffset,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .onSizeChanged { size ->
                                        topBarHeight = size.height
                                    },
                                state = topBarState
                            )
                        }
                        is HtmlHeader.FullScreenHeader -> {

                        }
                        HtmlHeader.None -> {}
                    }
                }
                else -> {}
            }
        },

        content = { paddingValues ->


            val original = with(density) {
                (dimensions.collapsingTopBar.maxHeight - topBarHeight.toDp()).toPx()
            }

            val topPaddingExtra = with(density) {
                original
                    .coerceAtLeast(minimumValue = 0f)
                    .toDp()
            }

            Log.d(
                "mirek",
                "extra: $topPaddingExtra  " +
                        "original: $original  " +
                        "maxH: ${dimensions.collapsingTopBar.maxHeight}  " +
                        "h: ${with(density) { topBarHeight.toDp() }}"
            )

            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(connection = nestedScrollConnection)
                    .padding(top = topPaddingExtra) ,
                state = listState,
                columns = GridCells.Fixed(count = config.spanCount),
                contentPadding = paddingValues,
                content = {
                    when (data) {
                        is HtmlData.Empty -> {
                            item(
                                span = { GridItemSpan(currentLineSpan = config.spanCount) }
                            ) {
                                Text(text = "TODO empty")
                            }
                        }

                        is HtmlData.Invalid -> {
                            item(
                                span = { GridItemSpan(currentLineSpan = config.spanCount) }
                            ) {
                                HtmlInvalid(data = data)
                            }
                        }

                        is HtmlData.Success -> {
                            item(
                                span = { GridItemSpan(currentLineSpan = config.spanCount) },
                            ) {
                                HtmlMetrics(monitoring = data.monitoring)
                            }

                            item(
                                span = { GridItemSpan(currentLineSpan = config.spanCount) }
                            ) {
                                MaterialColorPallete()
                            }

                            itemsIndexed(
                                span = { index, item -> GridItemSpan(currentLineSpan = item.span) },
                                items = data.htmlElements,
                                contentType = { intex, element -> element },
                                key = { index, element -> index }
                            ) { index, element ->
                                when (element) {
                                    is HtmlElement.Image -> HtmlImage(data = element)
                                    is HtmlElement.Quote -> HtmlQuoete(data = element)
                                    is HtmlElement.Table -> HtmlTable(data = element)
                                    is HtmlElement.TextBlock -> {
                                        Text(
                                            text = remember {
                                                element.text.toHtml()
                                                    .toSpannable()
                                                    .toAnnotatedString(primaryColor = colorScheme.primary)
                                            },
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                    }

                                    is HtmlElement.Gallery -> {
                                        HtmlPhotoGallery(gallery = element)
                                    }

                                    else -> throw IllegalStateException(
                                        "Element ${element.javaClass.simpleName} not supported yet!"
                                    )
                                }
                            }


                            item(
                                span = { GridItemSpan(currentLineSpan = config.spanCount) }
                            ) {
                                BrandingFooter()
                            }
                        }
                    }
                }
            )
        }
    )
}

*/