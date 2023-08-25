package mir.oslav.jet.html.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.text.toSpannable
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.composables.elements.HtmlImage
import mir.oslav.jet.html.composables.elements.HtmlInvalid
import mir.oslav.jet.html.composables.elements.HtmlQuoete
import mir.oslav.jet.html.composables.elements.HtmlTable
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.toAnnotatedString
import mir.oslav.jet.html.toHtml


/**
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
@Composable
fun JetHtml(
    modifier: Modifier = Modifier,
    data: HtmlData,
) {

    val colorScheme = MaterialTheme.colorScheme
    val configuration = LocalConfiguration.current

    val listState = rememberLazyListState()


    LaunchedEffect(key1 = configuration, block = {
        HtmlDimensions.init(configuration = configuration)
    })

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = listState,
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
                    itemsIndexed(items = data.htmlElements) { index, element ->
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

                        }
                    }
                }
            }
        }
    )
}