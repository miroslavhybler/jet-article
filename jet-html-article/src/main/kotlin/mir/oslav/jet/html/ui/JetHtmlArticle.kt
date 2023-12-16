@file:SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

package mir.oslav.jet.html.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
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
import androidx.compose.ui.unit.dp
import mir.oslav.jet.annotations.JetBenchmark
import mir.oslav.jet.annotations.JetExperimental
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.LocalHtmlDimensions
import mir.oslav.jet.html.ui.elements.HtmlAddress
import mir.oslav.jet.html.ui.elements.HtmlCode
import mir.oslav.jet.html.ui.elements.HtmlImage
import mir.oslav.jet.html.ui.elements.HtmlInvalid
import mir.oslav.jet.html.ui.elements.HtmlMetrics
import mir.oslav.jet.html.ui.elements.HtmlQuoete
import mir.oslav.jet.html.ui.elements.HtmlTable
import mir.oslav.jet.html.ui.elements.HtmlTextBlock
import mir.oslav.jet.html.ui.elements.HtmlTitle
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
@JetExperimental
fun JetHtmlArticle(
    modifier: Modifier = Modifier,
    data: HtmlData,
    config: HtmlConfig = remember { HtmlConfig() },
    gridState: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(all = 0.dp)
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
            config = config,
            gridState = gridState,
            contentPadding = contentPadding
        )
    }
}


@Composable
fun JetHtmlArticleContent(
    modifier: Modifier = Modifier,
    data: HtmlData,
    config: HtmlConfig = remember { HtmlConfig() },
    gridState: LazyGridState = rememberLazyGridState(),
    loading: @Composable () -> Unit = remember { { JetHtmlArticleDefaults.DefaultLoading() } },
    image: @Composable (HtmlElement.Parsed.Image) -> Unit = remember { { HtmlImage(data = it) } },
    quote: @Composable (HtmlElement.Parsed.Quote) -> Unit = remember { { HtmlQuoete(data = it) } },
    table: @Composable (HtmlElement.Parsed.Table) -> Unit = remember { { HtmlTable(data = it) } },
    address: @Composable (HtmlElement.Parsed.Address) -> Unit = remember { { HtmlAddress(address = it) } },
    text: @Composable (HtmlElement.Parsed.TextBlock) -> Unit = remember { { HtmlTextBlock(text = it) } },
    title: @Composable (HtmlElement.Parsed.Title) -> Unit = remember { { HtmlTitle(title = it) } },
    code: @Composable (HtmlElement.Parsed.Code) -> Unit = remember { { HtmlCode(code = it) } },
    contentPadding: PaddingValues = PaddingValues(all = 0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center
) {

    val dimensions = LocalHtmlDimensions.current


    if (data.loadingStates.isLoading && !data.loadingStates.isAppending) {
        loading()
        return
    }

    if (data.error != null) {
        HtmlInvalid(error = data.error)
        return
    }

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize(),
        state = gridState,
        columns = GridCells.Fixed(count = config.spanCount),
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        content = {
            item(
                span = { GridItemSpan(currentLineSpan = config.spanCount) },
            ) {
                HtmlMetrics(
                    metering = data.metering,
                    modifier = Modifier.statusBarsPadding()
                )
            }

            itemsIndexed(
                span = { index, item -> GridItemSpan(currentLineSpan = item.span) },
                items = data.elements,
                contentType = { intex, element -> element },
                key = { index, element -> index }
            ) { index, element ->
                when (element) {
                    //TODO handle links
                    is HtmlElement.Parsed.Image -> image(element)
                    is HtmlElement.Parsed.Quote -> quote(element)
                    is HtmlElement.Parsed.Table -> table(element)
                    is HtmlElement.Parsed.Address -> address(element)
                    is HtmlElement.Parsed.TextBlock -> text(element)
                    is HtmlElement.Parsed.Title -> title(element)
                    is HtmlElement.Parsed.Code -> code(element)
                    else -> throw IllegalStateException(
                        "Element ${element.javaClass.simpleName} not supported yet!"
                    )
                }
            }

            if (data.loadingStates.isAppending) {
                item(span = { GridItemSpan(currentLineSpan = config.spanCount) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimensions.sidePadding, vertical = 4.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(size = 24.dp)
                                .align(alignment = Alignment.Center)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(height = dimensions.baseLinePadding))
            }

        },
        contentPadding = contentPadding
    )
}


object JetHtmlArticleDefaults {


    @Composable
    fun DefaultLoading() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .safeContentPadding()
                .padding(top = 64.dp, bottom = 128.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier
            )
            Text(
                text = "Loading",
                modifier = Modifier
            )
        }
    }
}