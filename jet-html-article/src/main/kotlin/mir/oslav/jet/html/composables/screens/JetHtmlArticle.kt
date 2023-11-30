@file:SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

package mir.oslav.jet.html.composables.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import mir.oslav.jet.annotations.JetBenchmark
import mir.oslav.jet.annotations.JetExperimental
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.LocalHtmlDimensions
import mir.oslav.jet.html.composables.HtmlPhotoGallery
import mir.oslav.jet.html.composables.elements.HtmlAddress
import mir.oslav.jet.html.composables.elements.HtmlImage
import mir.oslav.jet.html.composables.elements.HtmlInvalid
import mir.oslav.jet.html.composables.elements.HtmlMetrics
import mir.oslav.jet.html.composables.elements.HtmlQuoete
import mir.oslav.jet.html.composables.elements.HtmlTable
import mir.oslav.jet.html.composables.elements.HtmlTextBlock
import mir.oslav.jet.html.composables.elements.HtmlTitle
import mir.oslav.jet.html.data.HtmlConfig
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.HtmlElement


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
    config: HtmlConfig = HtmlConfig(),
) {

    val configuration = LocalConfiguration.current

    val dimensions = remember(key1 = configuration) {
        HtmlDimensions().also { dimensions ->
            dimensions.init(
                configuration = configuration,
                screenWidth = configuration.screenWidthDp.dp,
                screenHeight = configuration.screenHeightDp.dp,
            )
        }
    }


    CompositionLocalProvider(LocalHtmlDimensions provides dimensions) {
        JetHtmlArticleContent(
            modifier = modifier,
            data = data,
            config = config
        )
    }
}


@Composable
fun JetHtmlArticleContent(
    modifier: Modifier = Modifier,
    data: HtmlData,
    config: HtmlConfig
) {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .safeContentPadding(),
        state = gridState,
        columns = GridCells.Fixed(count = config.spanCount),
        content = {
            when (data) {
                is HtmlData.Empty -> {
                    item(
                        span = { GridItemSpan(currentLineSpan = config.spanCount) }
                    ) {
                        Text(text = "TODO empty")
                    }
                }

                is HtmlData.Loading -> {

                    item(span = { GridItemSpan(currentLineSpan = config.spanCount) }) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(alignment = Alignment.Center)
                            )
                            Text(
                                text = "Loading",
                                modifier = Modifier.align(alignment = Alignment.Center)
                            )
                        }
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
                            monitoring = data.metrics,
                            modifier = Modifier
                        )
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
                }
            }
        }
    )
}