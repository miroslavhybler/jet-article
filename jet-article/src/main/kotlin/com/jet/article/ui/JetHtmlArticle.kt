@file:SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@file:Suppress("RedundantVisibilityModifier")

package com.jet.article.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.jet.article.ArticleParser
import com.jet.article.data.HtmlArticleData
import com.jet.article.data.HtmlElement
import com.jet.article.ui.elements.HtmlAddress
import com.jet.article.ui.elements.HtmlBasicList
import com.jet.article.ui.elements.HtmlCode
import com.jet.article.ui.elements.HtmlImage
import com.jet.article.ui.elements.HtmlQuoete
import com.jet.article.ui.elements.HtmlTable
import com.jet.article.ui.elements.HtmlTextBlock
import com.jet.article.ui.elements.HtmlTitle
import mir.oslav.jet.annotations.JetExperimental


/**
 * Default composable implementation for the library. To use custom layouts see [JetHtmlArticleContent].
 * It's basically [LazyColumn] with items being [HtmlElement].
 * @param modifier Modifier to modify composable
 * @param data Parsed html article data to be shown. Mostly result of [ArticleParser.parse].
 * @param listState [LazyListState] used to controll scroll.
 * @param contentPadding
 * @param header
 * @param footer
 * @param linkClickCallback
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 * @see JetHtmlArticleContent
 */
@Composable
@JetExperimental
public fun JetHtmlArticle(
    modifier: Modifier = Modifier,
    state: JetHtmlArticleState,
    contentPadding: PaddingValues = PaddingValues(all = 0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    header: @Composable LazyItemScope.() -> Unit = {},
    footer: @Composable LazyItemScope.() -> Unit = {},
) = trace(sectionName = "JetHtmlArticle") {
    JetHtmlArticleContent(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        header = header,
        footer = footer,
    )
}


/**
 * @since 1.0.0
 */
@Composable
public fun JetHtmlArticleContent(
    modifier: Modifier = Modifier,
    state: JetHtmlArticleState,
    contentPadding: PaddingValues = PaddingValues(all = 0.dp),
    text: @Composable (HtmlElement.TextBlock) -> Unit = {
        JetHtmlArticleDefaults.TextBlock(text = it)
    },
    image: @Composable (HtmlElement.Image) -> Unit = {
        JetHtmlArticleDefaults.Image(data = it)
    },
    quote: @Composable (HtmlElement.Quote) -> Unit = {
        JetHtmlArticleDefaults.Quoete(data = it)
    },
    table: @Composable (HtmlElement.Table) -> Unit = {
        JetHtmlArticleDefaults.Table(data = it)
    },
    address: @Composable (HtmlElement.Address) -> Unit = {
        JetHtmlArticleDefaults.Address(address = it)
    },
    title: @Composable (HtmlElement.Title) -> Unit = {
        JetHtmlArticleDefaults.Title(title = it)
    },
    code: @Composable (HtmlElement.Code) -> Unit = {
        JetHtmlArticleDefaults.Code(code = it)
    },
    basicList: @Composable (HtmlElement.BasicList) -> Unit = {
        JetHtmlArticleDefaults.List(list = it)
    },
    header: @Composable LazyItemScope. () -> Unit = {},
    footer: @Composable LazyItemScope.() -> Unit = {},
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
) = trace(sectionName = "JetHtmlArticleContent") {

    CompositionLocalProvider(
        /*
        LocalBaseArticleUrl provides state.data.url,
        LocalLinkHandler provides state.linkClickHandler,
        LocalHtmlArticleData provides state.data,


         */
    ) {
        if (state.isSelectionEnabled) {
            SelectionContainer {
                JetHtmlArticleLazyColumn(
                    modifier = modifier,
                    state = state,
                    contentPadding = contentPadding,
                    text = text,
                    image = image,
                    quote = quote,
                    table = table,
                    address = address,
                    title = title,
                    code = code,
                    basicList = basicList,
                    header = header,
                    footer = footer,
                    verticalArrangement = verticalArrangement,
                    horizontalAlignment = horizontalAlignment,
                )
            }
        } else {
            JetHtmlArticleLazyColumn(
                modifier = modifier,
                state = state,
                contentPadding = contentPadding,
                text = text,
                image = image,
                quote = quote,
                table = table,
                address = address,
                title = title,
                code = code,
                basicList = basicList,
                header = header,
                footer = footer,
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
            )
        }
    }
}


@Composable
private fun JetHtmlArticleLazyColumn(
    modifier: Modifier,
    state: JetHtmlArticleState,
    contentPadding: PaddingValues,
    text: @Composable (HtmlElement.TextBlock) -> Unit,
    image: @Composable (HtmlElement.Image) -> Unit,
    quote: @Composable (HtmlElement.Quote) -> Unit,
    table: @Composable (HtmlElement.Table) -> Unit,
    address: @Composable (HtmlElement.Address) -> Unit,
    title: @Composable (HtmlElement.Title) -> Unit,
    code: @Composable (HtmlElement.Code) -> Unit,
    basicList: @Composable (HtmlElement.BasicList) -> Unit,
    header: @Composable LazyItemScope. () -> Unit,
    footer: @Composable LazyItemScope.() -> Unit,
    verticalArrangement: Arrangement.Vertical,
    horizontalAlignment: Alignment.Horizontal,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = state.listState,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        contentPadding = contentPadding,
        content = {
            item(content = header)
            itemsIndexed(
                items = state.data.elements,
                key = { _, element -> element.key },
            ) { index, element ->
                when (element) {
                    is HtmlElement.Image -> image(element)
                    is HtmlElement.Quote -> quote(element)
                    is HtmlElement.Table -> table(element)
                    is HtmlElement.Address -> address(element)
                    is HtmlElement.TextBlock -> text(element)
                    is HtmlElement.Title -> {
                        if (index != 0) {
                            Column(modifier = Modifier) {
                                //TODO spacing
                                Spacer(modifier = Modifier.height(height = 24.dp))
                                title(element)
                            }
                        } else {
                            title(element)
                        }
                    }

                    is HtmlElement.Code -> code(element)
                    is HtmlElement.BasicList -> basicList(element)
                    else -> throw IllegalStateException(
                        "Element ${element.javaClass.simpleName} not supported yet!"
                    )
                }
            }

            if (state.data.isEmpty) {
                item {
                    Text(text = "EMPTY")
                }
            }
            item(content = footer)
        },
    )
}


/**
 * @since 1.0.0
 */
public object JetHtmlArticleDefaults {

    @Composable
    fun TextBlock(text: HtmlElement.TextBlock) {
        HtmlTextBlock(
            text = text,
        )
    }

    @Composable
    fun Image(data: HtmlElement.Image) {
        HtmlImage(data = data)
    }

    @Composable
    fun Quoete(data: HtmlElement.Quote) {
        HtmlQuoete(data = data)
    }

    @Composable
    fun Table(data: HtmlElement.Table) {
        HtmlTable(data = data)
    }

    @Composable
    fun Address(address: HtmlElement.Address) {
        HtmlAddress(address = address)
    }

    @Composable
    fun Title(title: HtmlElement.Title) {
        HtmlTitle(title = title)
    }

    @Composable
    fun List(list: HtmlElement.BasicList) {
        HtmlBasicList(list = list)
    }

    @Composable
    fun Code(code: HtmlElement.Code) {
        HtmlCode(code = code)
    }
}


public class JetHtmlArticleState internal constructor(
    val listState: LazyListState,
    initialIsSelectionEnabled: Boolean,
    initialData: HtmlArticleData,
) {
    var isSelectionEnabled: Boolean by mutableStateOf(value = initialIsSelectionEnabled)

    var data: HtmlArticleData by mutableStateOf(value = initialData)
        private set

    fun show(data: HtmlArticleData) {
        this.data = data
    }
}

@Composable
fun rememberJetHtmlArticleState(
    listState: LazyListState = rememberLazyListState(),
    initialIsSelectionEnabled: Boolean = false,
    initialData: HtmlArticleData = HtmlArticleData.empty,
): JetHtmlArticleState {
    return remember {
        JetHtmlArticleState(
            listState = listState,
            initialIsSelectionEnabled = initialIsSelectionEnabled,
            initialData = initialData,
        )
    }
}