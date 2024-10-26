@file:Suppress("RedundantVisibilityModifier", "RedundantUnitReturnType")

package com.jet.article

import android.content.Context
import androidx.annotation.CheckResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.trace
import androidx.core.text.toSpannable
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.withContext
import com.jet.article.data.HtmlContentType
import com.jet.article.data.HtmlArticleData
import com.jet.article.data.HtmlElement
import com.jet.article.data.HtmlHeadData
import com.jet.article.ui.LinkClickHandler
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext


/**
 * Main component of the library, enables you parse html content and get [HtmlArticleData].
 * @see com.jet.article.ui.JetHtmlArticle
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 03.01.2024
 */
public object ArticleParser {


    /**
     * Safe coroutine context for the parser and all of its work. Native code does not handle thread
     * safety at all!! So all things form kotlin code **has to be called on single thread** otherwise
     * you are at hight risk of getting native crash related to pointer memory issue.
     * @since 1.0.0
     */
    internal val safeCoroutineContext: CoroutineContext = Executors
        .newSingleThreadExecutor()
        .asCoroutineDispatcher()
        .plus(context = CoroutineName(name = "JetHtmlArticleParser"))


    /**
     * Holds flag if its simple text format enabled passed in [initialize].
     * @since 1.0.0
     */
    var isSimpleTextFormatAllowed: Boolean by mutableStateOf(value = true)
        private set

    var linkColor: Color = Color.Blue
        private set

    /**
     * Sets up input options for parser. Keep in mind that this function should be called before [parse].
     * @param areImagesEnabled True when you want to enable images from <img> tag being included in
     * output, false otherwise. False by default.
     * @param isLoggingEnabled True if you want to enable logs from native libs, false otherwise. False by default.
     * @param isSimpleTextFormatAllowed True if you want to use simple html formatting like bold, italic, ...
     * for the text. False otherwise. True by default. Keep in mind that putting false will also
     * disable links inside text blocks.
     * @param isQueringTextOutsideTextTags True if you want to query and show text that is outside of
     * causal text tags like <span>, <p>, .... False otherwise. False by default. Queried text
     * will be passed as [HtmlElement.TextBlock] in output data.
     * @since 1.0.0
     */
    public fun initialize(
        areImagesEnabled: Boolean = false,
        isLoggingEnabled: Boolean = false,
        isSimpleTextFormatAllowed: Boolean = true,
        isQueringTextOutsideTextTags: Boolean = false,
        linkColor: Color = Color.Blue,
    ): Unit {
        this.isSimpleTextFormatAllowed = isSimpleTextFormatAllowed
        this.linkColor = linkColor
        ParserNative.initialize(
            areImagesEnabled = areImagesEnabled,
            isLoggingEnabled = isLoggingEnabled,
            isSimpleTextFormatAllowed = isSimpleTextFormatAllowed,
            isQueringTextOutsideTextTags = isQueringTextOutsideTextTags,
        )
    }


    /**
     * Adds rule for text processing. At least one parameters should be set. Operator applied is AND.
     * @param tag Set if you want to exclude specific tag, like <p> -> "p"
     * @param clazz Set if you want to exclude specific class, like "menu"
     * @param id Set if you want to exclude tag with specific id, like "menu"
     * @param keyword Set if you want to exclude tag based on its id or class by
     * specific keyword, like "cookies"
     * @since 1.0.0
     */
    fun addExcludeOption(
        tag: String = "",
        clazz: String = "",
        id: String = "",
        keyword: String = "",
    ): Unit {
        ContentFilterNative.addExcludeOption(
            tag = tag,
            clazz = clazz,
            id = id,
            keyword = keyword,
        )
    }


    /**
     * Parses the [content] and creates [HtmlArticleData]. Don't forget to call [initialize] before
     * parsing.
     * @param content Html code
     * @param url Original url of the article
     * @since 1.0.0
     */
    @CheckResult
    public suspend fun parse(
        content: String,
        url: String,
        linkClickHandler: LinkClickHandler = LinkClickHandler(),
    ): HtmlArticleData = trace(sectionName = "ArticleParser#parse") {
        return withContext(context = safeCoroutineContext) parser@{
            val elements = ArrayList<HtmlElement>()
            ParserNative.setInput(content = content)

            if (!ParserNative.hasNextStep()) {
                return@parser HtmlArticleData(
                    elements = elements,
                    headData = HtmlHeadData(title = ParserNative.getTitle()),
                    url = url
                )
            }

            var key = 0
            while (ParserNative.hasNextStep()) {
                ParserNative.doNextStep()
                if (ParserNative.hasContent()) {
                    onElement(
                        elements = elements,
                        articleUrl = url,
                        newKey = ++key,
                        linkHandler = linkClickHandler,
                    )
                    ParserNative.resetCurrentContent()
                }
            }

            if (ParserNative.isAbortingWithError()) {
                val data = HtmlArticleData(
                    elements = elements,
                    headData = HtmlHeadData(title = ParserNative.getTitle()),
                    url = url,
                )
                ParserNative.clearAllResources()
                ContentFilterNative.clearAllResources()
                return@parser data
            }

            val data = HtmlArticleData(
                elements = elements,
                headData = HtmlHeadData(title = ParserNative.getTitle()),
                url = url,
            )
            ParserNative.clearAllResources()
            ContentFilterNative.clearAllResources()
            return@parser data
        }
    }


    /**
     * @since 1.0.0
     */
    private suspend fun onElement(
        elements: MutableList<HtmlElement>,
        articleUrl: String,
        newKey: Int,
        linkHandler: LinkClickHandler,
    ) = trace(sectionName = "ArticleParser#onElement()") {
        val type = ParserNative.getContentType()
        if (type == HtmlContentType.NO_CONTENT) {
            //Some weird error, this should never happen but teoretically it can since elements
            //parts are stored separately on c++ side.
            return@trace
        }
        when (type) {
            HtmlContentType.IMAGE -> {
                onImage(key = newKey, elements = elements, articleUrl = articleUrl)
            }

            HtmlContentType.TEXT -> {
                onText(key = newKey, elements = elements,linkHandler=linkHandler,)
            }

            HtmlContentType.TITLE -> {
                val content = ParserNative.getContent()
                if (content.isBlank()) {
                    return
                }
                elements.add(
                    element = HtmlElement.Title(
                        text = content,
                        titleTag = ParserNative.getCurrentTag(),
                        id = ParserNative.getCurrentTagId(),
                        key = newKey,
                    )
                )
            }

            HtmlContentType.LIST -> {
                val isOrdered = ParserNative.getCurrentTag() == "ol"
                val itemsList = ArrayList<String>().apply {
                    val listSize = ParserNative.getContentListSize()
                    for (i in 0 until listSize) {
                        add(ParserNative.getContentListItem(index = i))
                    }
                }
                elements.add(
                    element = HtmlElement.BasicList(
                        isOrdered = isOrdered,
                        items = itemsList,
                        id = ParserNative.getCurrentTagId(),
                        key = newKey,
                    )
                )
            }

            HtmlContentType.QUOTE -> {
                val content = ParserNative.getContent()
                if (content.isBlank()) {
                    return
                }
                elements.add(
                    element = HtmlElement.Quote(
                        text = content,
                        id = ParserNative.getCurrentTagId(),
                        key = newKey,
                    ),
                )
            }

            HtmlContentType.CODE -> {
                val content = ParserNative.getContent()
                if (content.isBlank()) {
                    return
                }
                elements.add(
                    element = HtmlElement.Code(
                        content = content,
                        id = ParserNative.getCurrentTagId(),
                        key = newKey,
                    )
                )
            }

            HtmlContentType.TABLE -> {
                onTable(key = newKey, elements = elements)
            }

            HtmlContentType.ADDRESS -> {
                val content = ParserNative.getContent()

                if (content.isBlank()) {
                    return
                }
                elements.add(
                    element = HtmlElement.TextBlock(
                        text = buildAnnotatedString {
                            append(text = content)
                        },
                        id = ParserNative.getCurrentTagId(),
                        key = newKey,
                    )
                )
            }

            else -> {}
        }
    }


    private fun onText(
        elements: MutableList<HtmlElement>,
        key: Int,
        linkHandler: LinkClickHandler,
    ) {
        val content = ParserNative.getContent()

        if (content.isBlank()) {
            return
        }

        val finalContent = if (isSimpleTextFormatAllowed) {
            content.toHtml()
                .toSpannable()
                .toAnnotatedString(
                    primaryColor = linkColor,
                    linkClickHandler = linkHandler,
                )
        } else {
            buildAnnotatedString {
                append(text = content)
            }
        }
        elements.add(
            element = HtmlElement.TextBlock(
                text = finalContent,
                id = ParserNative.getCurrentTagId(),
                key = key,
            )
        )
    }


    private fun onImage(
        elements: MutableList<HtmlElement>,
        articleUrl: String,
        key: Int,
    ) {
        var imageUrl: String = ParserNative.getContentMapItem(attributeName = "src")
        if (
            imageUrl.isEmpty()
            || imageUrl.endsWith(suffix = ".svg")
        ) {
            //Svg format not supported
            return
        }
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            val base = articleUrl.toDomainName()?.removeSuffix(suffix = "/")
            val end = imageUrl.removePrefix(prefix = "/")
            imageUrl = "www.$base/$end"
        }

        val w = ParserNative.getContentMapItem(attributeName = "width").toIntOrNull()
        val h = ParserNative.getContentMapItem(attributeName = "height").toIntOrNull()
        val alt = ParserNative.getContentMapItem(attributeName = "alt")

        val size = if (w != null && h != null)
            IntSize(width = w, height = h)
        else
            IntSize.Zero

        elements.add(
            element = HtmlElement.Image(
                url = imageUrl,
                description = alt,
                defaultSize = size,
                alt = alt,
                id = ParserNative.getCurrentTagId(),
                key = key,
            )
        )
    }


    private fun onTable(
        elements: MutableList<HtmlElement>,
        key: Int,
    ) {
        val rows = ArrayList<HtmlElement.Table.TableRow>()

        val columnCount = ParserNative.getTableColumnCount()
        val rowsCount = ParserNative.getTableRowsCount()

        if (rowsCount == 0 || columnCount == 0) {
            return
        }

        for (i in 0 until rowsCount) {
            val columns = ArrayList<String>()
            for (j in 0 until columnCount) {
                val el = ParserNative.getTableCell(row = i, column = j)
                columns.add(element = el)
            }
            rows.add(
                element = HtmlElement.Table.TableRow(
                    values = columns.mapIndexed { i, value ->
                        HtmlElement.Table.TableRow.TableCell(
                            columnKey = i,
                            value = value
                        )
                    },
                    rowKey = i
                )
            )
        }


        elements.add(
            element = HtmlElement.Table(
                rows = rows,
                id = ParserNative.getCurrentTagId(),
                key = key,
            )
        )
    }


    /**
     * Holds util functions which can be used independentely from [ArticleParser]
     * @since 1.0.0
     */
    object Utils {


        /**
         * @param input
         * @return
         * @since 1.0.0
         */
        fun clearTagsFromText(
            input: String
        ): String {
            return UtilsNative.clearTagsFromText(input = input)
        }


        /**
         * @param input
         * @return
         * @since 1.0.0
         */
        fun clearTagsAndReplaceEntitiesFromText(
            input: String
        ): String {
            return UtilsNative.clearTagsAndReplaceEntitiesFromText(input = input)
        }
    }
}