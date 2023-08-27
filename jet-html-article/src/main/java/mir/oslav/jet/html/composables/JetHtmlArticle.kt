package mir.oslav.jet.html.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.dp
import androidx.core.text.toSpannable
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import mir.oslav.jet.annotations.JetBenchmark
import mir.oslav.jet.annotations.JetExperimental
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.composables.elements.HtmlImage
import mir.oslav.jet.html.composables.elements.HtmlInvalid
import mir.oslav.jet.html.composables.elements.HtmlMetrics
import mir.oslav.jet.html.composables.elements.HtmlQuoete
import mir.oslav.jet.html.composables.elements.HtmlTable
import mir.oslav.jet.html.composables.elements.topbars.HtmlTopBarSimple
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
fun JetHtmlArticle(
    modifier: Modifier = Modifier,
    data: HtmlData,
    config: HtmlConfig = HtmlConfig(spanCount = 1),
    navHostController: NavHostController
) {

    val colorScheme = MaterialTheme.colorScheme
    val configuration = LocalConfiguration.current

    val systemUiController = rememberSystemUiController()
    val listState = rememberLazyGridState()

    HtmlDimensions.init(configuration = configuration)
    var fullScrollOffset by remember { mutableFloatStateOf(value = 0f) }
    var backgroundAlpha by remember { mutableFloatStateOf(value = 0f) }
    var elevation by remember { mutableStateOf(value = 0.dp) }
    var topBarHeight by remember { mutableIntStateOf(value = 0) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val offset = super.onPreScroll(available, source)
                fullScrollOffset -= available.y
                return offset
            }
        }
    }

    LaunchedEffect(key1 = systemUiController, block = {
        systemUiController.setSystemBarsColor(color = Color.White, darkIcons = true)
    })

    LaunchedEffect(key1 = fullScrollOffset, block = {
        if (topBarHeight != 0) {
            backgroundAlpha = ((fullScrollOffset) / topBarHeight)
                .coerceIn(minimumValue = 0f, maximumValue = 1f)

            elevation = ((fullScrollOffset / topBarHeight))
                .coerceIn(minimumValue = 0f, maximumValue = 6f).dp
        }
    })


    Scaffold(
        topBar = {
            when (data) {
                is HtmlData.Success -> {
                    when (data.header) {
                        is HtmlHeader.TopBarHeader -> {
                            HtmlTopBarSimple(
                                navHostController = navHostController,
                                title = data.title,
                                shadow = elevation,
                                backgroundAlpha = backgroundAlpha,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .onSizeChanged { size ->
                                        topBarHeight = size.height
                                    }
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
            LazyVerticalGrid(
                modifier = modifier
                    .fillMaxSize()
                    .nestedScroll(connection = nestedScrollConnection),

                state = listState,
                columns = GridCells.Fixed(count = config.spanCount),
                contentPadding = paddingValues,
                content = {
                    when (data) {
                        is HtmlData.Empty -> {
                            item {
                                Text(text = "TODO empty")
                            }
                        }

                        is HtmlData.Invalid -> {
                            item {
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