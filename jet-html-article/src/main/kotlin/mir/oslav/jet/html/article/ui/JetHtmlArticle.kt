@file:SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

package mir.oslav.jet.html.article.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import mir.oslav.jet.annotations.JetExperimental
import mir.oslav.jet.html.article.HtmlDimensions
import mir.oslav.jet.html.article.LocalHtmlDimensions
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
            listState = listState,
            contentPadding = contentPadding,
            header = header,
            footer = footer
        )
    }
}


@Composable
fun JetHtmlArticleContent(
    modifier: Modifier = Modifier,
    data: HtmlData,
    listState: LazyListState = rememberLazyListState(),
    loading: @Composable () -> Unit = remember { { JetHtmlArticleDefaults.DefaultLoading() } },
    image: @Composable (HtmlElement.Image) -> Unit = remember { { HtmlImage(data = it) } },
    quote: @Composable (HtmlElement.Quote) -> Unit = remember { { HtmlQuoete(data = it) } },
    table: @Composable (HtmlElement.Table) -> Unit = remember { { HtmlTable(data = it) } },
    address: @Composable (HtmlElement.Address) -> Unit = remember { { HtmlAddress(address = it) } },
    text: @Composable (HtmlElement.TextBlock) -> Unit = remember { { HtmlTextBlock(text = it) } },
    title: @Composable (HtmlElement.Title) -> Unit = remember { { HtmlTitle(title = it) } },
    code: @Composable (HtmlElement.Code) -> Unit = remember { { HtmlCode(code = it) } },
    header: @Composable LazyItemScope. () -> Unit = {},
    footer: @Composable LazyItemScope.() -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(all = 0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top
) {

    val dimensions = LocalHtmlDimensions.current


    if (data.loadingStates.isLoading && !data.loadingStates.isAppending) {
        loading()
        return
    }

    if (data.error != null) {
        HtmlInvalid(error = data.error!!)
        return
    }

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
                    else -> throw IllegalStateException(
                        "Element ${element.javaClass.simpleName} not supported yet!"
                    )
                }
            }

            if (data.loadingStates.isAppending) {
                item() {
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

            item(content = footer)

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