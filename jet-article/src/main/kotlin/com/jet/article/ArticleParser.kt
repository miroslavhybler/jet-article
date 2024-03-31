@file:Suppress("RedundantVisibilityModifier")

package com.jet.article

import android.content.Context
import androidx.compose.ui.unit.IntSize
import com.jet.article.data.ErrorCode
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.jet.article.data.HtmlContentType
import com.jet.article.data.HtmlArticleData
import com.jet.article.data.HtmlElement
import com.jet.article.data.HtmlHeadData
import kotlin.coroutines.CoroutineContext


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 03.01.2024
 */
public object ArticleParser {


    /**
     * Version of the parsing library
     * @since 1.0.0
     */
    public const val version: String = "1.0.0"


    /**
     * @since 1.0.0
     */
    private val safeCoroutineContext: CoroutineContext = Dispatchers.Default
        .plus(context = CoroutineName(name = "JetHtmlArticleParse"))


    /**
     * Parses the [content] and creates [HtmlArticleData]
     * @param content Html code
     * @param url Original url of the article
     * @since 1.0.0
     */
    public suspend fun parse(
        content: String,
        url: String,
    ): HtmlArticleData {
        return withContext(context = safeCoroutineContext) parser@{
            val elements = ArrayList<HtmlElement>()
            ParserNative.setInput(content = content)

            if (!ParserNative.hasNextStep()) {
                return@parser HtmlArticleData(
                    failure = HtmlArticleData.Failure(
                        message = "Content is empty",
                        code = ErrorCode.NO_ERROR
                    ),
                    elements = elements,
                    headData = HtmlHeadData(title = ParserNative.getTitle()),
                    url = url
                )
            }

            while (ParserNative.hasNextStep()) {
                ParserNative.doNextStep()
                if (ParserNative.hasContent()) {
                    onElement(elements = elements, articleUrl = url)
                    ParserNative.resetCurrentContent()
                }
            }

            if (ParserNative.isAbortingWithError()) {
                val tag = ParserNative.getCurrentTag()
                val data = HtmlArticleData(
                    elements = elements,
                    headData = HtmlHeadData(title = ParserNative.getTitle()),
                    failure = HtmlArticleData.Failure(
                        message = "Error while processing $tag\nOriginal message:\n" + ParserNative.getErrorMessage(),
                        code = ParserNative.getErrorCode(),
                    ),
                    url = url,
                )
                ParserNative.clearAllResources()
                ProcessorNative.clearAllResources()
                return@parser data
            }

            val data = HtmlArticleData(
                elements = elements,
                headData = HtmlHeadData(title = ParserNative.getTitle()),
                url = url,
            )
            ParserNative.clearAllResources()
            ProcessorNative.clearAllResources()
            return@parser data
        }
    }


    /**
     * @since 1.0.0
     */
    private suspend fun onElement(
        elements: MutableList<HtmlElement>,
        articleUrl: String
    ) {
        val type = ParserNative.getContentType()
        if (type == HtmlContentType.NO_CONTENT) {
            return
        }
        when (type) {
            HtmlContentType.IMAGE -> {
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
                        id = ParserNative.getCurrentTagId()
                    )
                )
            }

            HtmlContentType.TEXT -> {
                val content = ParserNative.getContent()

                if (content.isBlank()) {
                    return
                }

                val text = HtmlElement.TextBlock(
                    text = content,
                    id = ParserNative.getCurrentTagId()
                )
                elements.add(element = text)
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
                        id = ParserNative.getCurrentTagId()
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
                        id = ParserNative.getCurrentTagId()
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
                        id = ParserNative.getCurrentTagId()
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
                        id = ParserNative.getCurrentTagId()
                    )
                )
            }

            HtmlContentType.TABLE -> {
                val rows = ArrayList<List<String>>()

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
                    rows.add(columns)
                }


                elements.add(
                    element = HtmlElement.Table(
                        rows = rows,
                        id = ParserNative.getCurrentTagId()
                    )
                )
            }

            else -> {}
        }
    }
}