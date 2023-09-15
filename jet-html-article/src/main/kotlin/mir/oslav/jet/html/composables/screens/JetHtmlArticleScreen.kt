@file:SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

package mir.oslav.jet.html.composables.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
import mir.oslav.jet.html.data.HtmlConfig
import mir.oslav.jet.html.composables.elements.HtmlAddress
import mir.oslav.jet.html.composables.elements.HtmlInvalid
import mir.oslav.jet.html.composables.elements.HtmlMetrics
import mir.oslav.jet.html.composables.elements.HtmlQuoete
import mir.oslav.jet.html.composables.elements.HtmlTable
import mir.oslav.jet.html.composables.elements.HtmlTextBlock
import mir.oslav.jet.html.composables.elements.HtmlImage
import mir.oslav.jet.html.composables.HtmlPhotoGallery
import mir.oslav.jet.html.composables.topbars.CollapsingTopBarState
import mir.oslav.jet.html.composables.topbars.HtmlCollapsingTopBar
import mir.oslav.jet.html.composables.topbars.HtmlTopBarSimple
import mir.oslav.jet.html.composables.topbars.rememberCollapsingTopBarState
import mir.oslav.jet.html.composables.JetHtmlArticleScaffold
import mir.oslav.jet.html.composables.elements.HtmlTitle
import mir.oslav.jet.html.composables.rememberJetHtmlArticleScaffoldState
import mir.oslav.jet.html.composables.topbars.rememberAppearingTopBarState
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.utils.navigationBarsPadding
import mir.oslav.jet.utils.statusBarsPadding
import mir.oslav.jet.utils.theme.MaterialColors
import kotlin.math.abs


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
    config: HtmlConfig = HtmlConfig(),
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
    val statusBarHeight = density.statusBarsPadding()
    val navigationBarHeight = density.navigationBarsPadding()
    LaunchedEffect(
        key1 = Unit,
        block = {
            dimensions = HtmlDimensions().also { dimensions ->
                dimensions.init(
                    configuration = configuration,
                    screenWidth = configuration.screenWidthDp.dp,
                    screenHeight = configuration.screenHeightDp.dp,
                    statusBarHeight = statusBarHeight,
                    navigationBarHeight = navigationBarHeight
                )
            }
            systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = true)
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

        JetHtmlArticleScaffold(
            modifier = modifier
                .fillMaxSize(),

            config = config,
            scaffoldState = rememberJetHtmlArticleScaffoldState(
                topBarState = topBarState,
                config = config
            ),
            topBar = {
                HtmlTopBar(
                    data = data,
                    navHostController = navHostController,
                    config = config,
                    modifier = Modifier,
                    collapsingTopBarState = topBarState
                )
            },
            content = {// paddingValues ->

                //TODO proper landscape support
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = gridState,
                    columns = GridCells.Fixed(count = config.spanCount),
                    contentPadding = PaddingValues(
                        bottom = density.navigationBarsPadding()
                    ),
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
                                    MaterialColors()
                                }

                                itemsIndexed(
                                    span = { index, item -> GridItemSpan(currentLineSpan = item.span) },
                                    items = data.elements,
                                    contentType = { intex, element -> element },
                                    key = { index, element -> index }
                                ) { index, element ->
                                    when (element) {
                                        is HtmlElement.Parsed.Image -> HtmlImage(data = element)
                                        is HtmlElement.Parsed.Quote -> HtmlQuoete(data = element)
                                        is HtmlElement.Parsed.Table -> HtmlTable(data = element)
                                        is HtmlElement.Parsed.Address -> HtmlAddress(address = element)
                                        is HtmlElement.Parsed.TextBlock -> HtmlTextBlock(text = element)
                                        is HtmlElement.Parsed.Title -> HtmlTitle(title = element)

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
                                    BrandingFooter(modifier = Modifier.navigationBarsPadding())
                                }
                            }
                        }
                    }
                )
            }
        )
    }
}


@Composable
private fun HtmlTopBar(
    modifier: Modifier = Modifier,
    data: HtmlData,
    navHostController: NavHostController,
    config: HtmlConfig,
    collapsingTopBarState: CollapsingTopBarState
) {
    when (data) {
        is HtmlData.Success -> {
            if (data.topBar != null) {
                when (config.topBarConfig) {
                    HtmlConfig.TopBarConfig.SIMPLE -> {
                        HtmlTopBarSimple(
                            navHostController = navHostController,
                            title = data.title,
                            modifier = modifier,
                            state = rememberAppearingTopBarState()
                        )
                    }

                    HtmlConfig.TopBarConfig.APPEARING -> {
                        HtmlTopBarSimple(
                            navHostController = navHostController,
                            title = data.title,
                            modifier = modifier,
                            state = rememberAppearingTopBarState()
                        )
                    }

                    HtmlConfig.TopBarConfig.COLLAPSING -> {
                        HtmlCollapsingTopBar(
                            navHostController = navHostController,
                            title = data.title,
                            state = collapsingTopBarState,
                            modifier = modifier
                        )
                    }

                    HtmlConfig.TopBarConfig.NONE -> return
                    else -> throw IllegalStateException("Not implemented yet!")
                }
            }
        }

        else -> {}
    }
}