package mir.oslav.jet.html.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.text.toSpannable
import mir.oslav.jet.annotations.JetBenchmark
import mir.oslav.jet.annotations.JetExperimental
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.composables.elements.HtmlImage
import mir.oslav.jet.html.composables.elements.HtmlInvalid
import mir.oslav.jet.html.composables.elements.HtmlMetrics
import mir.oslav.jet.html.composables.elements.HtmlQuoete
import mir.oslav.jet.html.composables.elements.HtmlTable
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.toAnnotatedString
import mir.oslav.jet.html.toHtml


/**
 * @param modifier
 * @param data
 * @param spanCount
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
    spanCount: Int
) {

    val colorScheme = MaterialTheme.colorScheme
    val configuration = LocalConfiguration.current

    val listState = rememberLazyGridState()

    HtmlDimensions.init(configuration = configuration)

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize(),
        state = listState,
        columns = GridCells.Fixed(count = spanCount),
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
                        span = { GridItemSpan(currentLineSpan = spanCount) }
                    ) {
                        HtmlMetrics(monitoring = data.monitoring)
                    }

                    itemsIndexed(
                        span = { index, item -> GridItemSpan(currentLineSpan = spanCount) },
                        items = data.htmlElements
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
                                "Element ${element.javaClass.simpleName} not suppported yet!"
                            )
                        }
                    }
                }
            }
        }
    )
}