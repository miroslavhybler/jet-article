@file:SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

package mir.oslav.jet.html.article.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import mir.oslav.jet.annotations.JetExperimental
import mir.oslav.jet.html.article.ui.elements.HtmlAddress
import mir.oslav.jet.html.article.ui.elements.HtmlCode
import mir.oslav.jet.html.article.ui.elements.HtmlImage
import mir.oslav.jet.html.article.ui.elements.HtmlInvalid
import mir.oslav.jet.html.article.ui.elements.HtmlQuoete
import mir.oslav.jet.html.article.ui.elements.HtmlTable
import mir.oslav.jet.html.article.ui.elements.HtmlTextBlock
import mir.oslav.jet.html.article.ui.elements.HtmlTitle
import mir.oslav.jet.html.article.data.HtmlData
import mir.oslav.jet.html.article.data.HtmlElement
import mir.oslav.jet.html.article.ui.elements.HtmlBasicList


/**
 * @param modifier
 * @param data
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
@Composable
@JetExperimental
fun JetHtmlArticle(
    modifier: Modifier = Modifier,
    data: HtmlData,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(all = 0.dp),
    header: @Composable LazyItemScope.() -> Unit = {},
    footer: @Composable LazyItemScope.() -> Unit = {}
) {
    JetHtmlArticleContent(
        modifier = modifier,
        data = data,
        listState = listState,
        contentPadding = contentPadding,
        header = header,
        footer = footer
    )

}


@Composable
fun JetHtmlArticleContent(
    modifier: Modifier = Modifier,
    data: HtmlData,
    listState: LazyListState = rememberLazyListState(),
    image: @Composable (HtmlElement.Image) -> Unit = remember { { HtmlImage(data = it) } },
    quote: @Composable (HtmlElement.Quote) -> Unit = remember { { HtmlQuoete(data = it) } },
    table: @Composable (HtmlElement.Table) -> Unit = remember { { HtmlTable(data = it) } },
    address: @Composable (HtmlElement.Address) -> Unit = remember { { HtmlAddress(address = it) } },
    text: @Composable (HtmlElement.TextBlock) -> Unit = remember { { HtmlTextBlock(text = it) } },
    title: @Composable (HtmlElement.Title) -> Unit = remember { { HtmlTitle(title = it) } },
    code: @Composable (HtmlElement.Code) -> Unit = remember { { HtmlCode(code = it) } },
    basicList: @Composable (HtmlElement.BasicList) -> Unit = remember { { HtmlBasicList(list = it) } },
    header: @Composable LazyItemScope. () -> Unit = {},
    footer: @Composable LazyItemScope.() -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(all = 0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top
) {

    when (data) {
        is HtmlData.Empty -> Unit
        is HtmlData.Failure -> HtmlInvalid(error = data)
        is HtmlData.Success -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize(),
                state = listState,
                verticalArrangement = verticalArrangement,
                content = {
                    item(content = header)

                    itemsIndexed(
                        items = data.elements,
                    ) { index, element ->
                        when (element) {
                            //TODO handle links
                            is HtmlElement.Image -> image(element)
                            is HtmlElement.Quote -> quote(element)
                            is HtmlElement.Table -> table(element)
                            is HtmlElement.Address -> address(element)
                            is HtmlElement.TextBlock -> text(element)
                            is HtmlElement.Title -> title(element)
                            is HtmlElement.Code -> code(element)
                            is HtmlElement.BasicList -> basicList(element)
                            else -> throw IllegalStateException(
                                "Element ${element.javaClass.simpleName} not supported yet!"
                            )
                        }
                    }

                    item(content = footer)
                },
                contentPadding = contentPadding
            )
        }

    }
}