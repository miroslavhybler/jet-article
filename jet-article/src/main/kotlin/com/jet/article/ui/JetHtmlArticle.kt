@file:SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@file:Suppress("RedundantVisibilityModifier")

package com.jet.article.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mir.oslav.jet.annotations.JetExperimental
import com.jet.article.ui.elements.HtmlAddress
import com.jet.article.ui.elements.HtmlCode
import com.jet.article.ui.elements.HtmlImage
import com.jet.article.ui.elements.HtmlInvalid
import com.jet.article.ui.elements.HtmlQuoete
import com.jet.article.ui.elements.HtmlTable
import com.jet.article.ui.elements.HtmlTextBlock
import com.jet.article.ui.elements.HtmlTitle
import com.jet.article.data.HtmlArticleData
import com.jet.article.data.HtmlElement
import com.jet.article.ui.elements.HtmlBasicList


/**
 * Default composable implementation for the library. To use custom layouts see [JetHtmlArticleContent].
 * @param modifier Modifier to modify composable
 * @param data Parsed html article data to be shown.
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 * @see JetHtmlArticleContent
 */
@Composable
@JetExperimental
public fun JetHtmlArticle(
    modifier: Modifier = Modifier,
    data: HtmlArticleData,
    listState: LazyListState = rememberLazyListState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    contentPadding: PaddingValues = PaddingValues(all = 0.dp),
    header: @Composable LazyItemScope.() -> Unit = {},
    footer: @Composable LazyItemScope.() -> Unit = {},
    linkClickCallback: LinkClickHandler.LinkCallback = rememberDefaultLinkCallback(
        snackbarHostState = snackbarHostState,
        coroutineScope = rememberCoroutineScope(),
    ),
    colors: JetHtmlArticleColors = if (isSystemInDarkTheme())
        JetHtmlArticleDefaults.darkColorScheme
    else
        JetHtmlArticleDefaults.lightColorScheme
) {
    JetHtmlArticleContent(
        modifier = modifier,
        data = data,
        listState = listState,
        contentPadding = contentPadding,
        header = header,
        footer = footer,
        snackbarHostState = snackbarHostState,
        colors = colors,
        linkClickCallback = linkClickCallback
    )
}


/**
 * @since 1.0.0
 */
@Composable
public fun JetHtmlArticleContent(
    modifier: Modifier = Modifier,
    data: HtmlArticleData,
    listState: LazyListState = rememberLazyListState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    image: @Composable (HtmlElement.Image) -> Unit = { HtmlImage(data = it) },
    quote: @Composable (HtmlElement.Quote) -> Unit = { HtmlQuoete(data = it) },
    table: @Composable (HtmlElement.Table) -> Unit = { HtmlTable(data = it) },
    address: @Composable (HtmlElement.Address) -> Unit = { HtmlAddress(address = it) },
    text: @Composable (HtmlElement.TextBlock) -> Unit = { HtmlTextBlock(text = it) },
    title: @Composable (HtmlElement.Title) -> Unit = { HtmlTitle(title = it) },
    code: @Composable (HtmlElement.Code) -> Unit = { HtmlCode(code = it) },
    basicList: @Composable (HtmlElement.BasicList) -> Unit = { HtmlBasicList(list = it) },
    header: @Composable LazyItemScope. () -> Unit = {},
    footer: @Composable LazyItemScope.() -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(all = 0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(space = 8.dp),
    linkClickCallback: LinkClickHandler.LinkCallback = rememberDefaultLinkCallback(
        snackbarHostState = snackbarHostState,
        coroutineScope = rememberCoroutineScope(),
    ),
    colors: JetHtmlArticleColors = if (isSystemInDarkTheme())
        JetHtmlArticleDefaults.darkColorScheme
    else
        JetHtmlArticleDefaults.lightColorScheme
) {

    val linkHandler = rememberLinkClickHandler(
        lazyListState = listState,
        snackbarHostState = snackbarHostState,
        callback = linkClickCallback
    )

    CompositionLocalProvider(
        LocalBaseArticleUrl provides data.url,
        LocalLinkHandler provides linkHandler,
        LocalHtmlArticleData provides data,
        LocalContentPadding provides contentPadding,
        LocalColorScheme provides colors
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            content = {
                SelectionContainer {
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

                            if (data.isEmpty) {
                                item {
                                    Text(text = "EMPTY")
                                }
                            }
                            item(content = footer)
                        },
                        contentPadding = contentPadding
                    )

                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) {
                    Snackbar(
                        snackbarData = it,
                        modifier = Modifier.padding(
                            bottom = contentPadding.calculateBottomPadding()
                        )
                    )
                }
            }
        )
    }
}


/**
 * @since 1.0.0
 */
public object JetHtmlArticleDefaults {


    /**
     * @since 1.0.0
     */
    val lightColorScheme: JetHtmlArticleColors = getDefaultColors(
        colorScheme = lightColorScheme()
    )


    /**
     * @since 1.0.0
     */
    val darkColorScheme: JetHtmlArticleColors = getDefaultColors(
        colorScheme = darkColorScheme()
    )


    /**
     * @since 1.0.0
     */
    private fun getDefaultColors(
        colorScheme: ColorScheme
    ): JetHtmlArticleColors = JetHtmlArticleColors(
        textColor = colorScheme.onBackground,
        linkColor = colorScheme.primary,
        quoteTextColor = colorScheme.onBackground,
        quoteBarColor = colorScheme.tertiary,

        codeBackgroundColor = colorScheme.surface,
        codeBorderColor = colorScheme.primary,
        codeTextColor = colorScheme.onSurface,

        tableTextColor = colorScheme.onPrimaryContainer,
        tableBackgroundColor = colorScheme.primaryContainer
    )
}


/**
 * @since 1.0.0
 */
public class JetHtmlArticleColors public constructor(
    val textColor: Color,
    val linkColor: Color,
    val quoteTextColor: Color,
    val quoteBarColor: Color,
    val codeBorderColor: Color,
    val codeBackgroundColor: Color,
    val codeTextColor: Color,
    val tableBackgroundColor: Color,
    val tableTextColor: Color,
)


/**
 * @since 1.0.0
 */
internal val LocalLinkHandler: ProvidableCompositionLocal<LinkClickHandler?> = compositionLocalOf(
    defaultFactory = { null }
)


/**
 * @since 1.0.0
 */
internal val LocalBaseArticleUrl: ProvidableCompositionLocal<String> = compositionLocalOf(
    defaultFactory = { "" }
)


/**
 * @since 1.0.0
 */
internal val LocalHtmlArticleData: ProvidableCompositionLocal<HtmlArticleData> = compositionLocalOf(
    defaultFactory = HtmlArticleData::empty
)


/**
 * @since 1.0.0
 */
internal val LocalContentPadding: ProvidableCompositionLocal<PaddingValues> = compositionLocalOf(
    defaultFactory = { PaddingValues() }
)


/**
 * @since 1.0.0
 */
internal val LocalColorScheme: ProvidableCompositionLocal<JetHtmlArticleColors> =
    compositionLocalOf(
        defaultFactory = JetHtmlArticleDefaults::lightColorScheme
    )