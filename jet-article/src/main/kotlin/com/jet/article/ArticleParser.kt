package com.jet.article

import android.content.Context
import android.util.Log
import androidx.compose.ui.unit.IntSize
import coil.size.Size
import com.jet.article.data.ErrorCode
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.jet.article.data.HtmlContentType
import com.jet.article.data.HtmlData
import com.jet.article.data.HtmlElement
import com.jet.article.data.HtmlHeadData
import kotlin.coroutines.CoroutineContext


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 03.01.2024
 */
object ArticleParser {

    /**
     * Version of the parsing library
     * @since 1.0.0
     */
    const val version: String = "1.0.0-alpha01"


    /**
     * @since 1.0.0
     */
    private val safeCoroutineContext: CoroutineContext = Dispatchers.Default
        .plus(context = CoroutineName(name = "JetHtmlArticleParse"))


    suspend fun warmup(context: Context) {
        val file = context.resources.assets.open("warm-up.html")
        ParserNative.warmup(content = String(file.readBytes()))
    }


    /**
     * @since 1.0.0
     */
    suspend fun parse(
        content: String
    ): HtmlData {
        return withContext(context = safeCoroutineContext) parser@{
            val elements = ArrayList<HtmlElement>()
            ParserNative.setInput(content = content)

            if (!ParserNative.hasNextStep()) {
                return@parser HtmlData(
                    failure = HtmlData.Failure(
                        message = "Content is empty",
                        code = ErrorCode.NO_ERROR
                    ),
                    elements = elements,
                    headData = HtmlHeadData(
                        title = ParserNative.getTitle(),
                        baseUrl = ParserNative.getBase()
                    )
                )
            }

            while (ParserNative.hasNextStep()) {
                ParserNative.doNextStep()
                if (ParserNative.hasContent()) {
                    onElement(elements = elements)
                    ParserNative.resetCurrentContent()
                }
            }

            if (ParserNative.isAbortingWithError()) {
                val tag = ParserNative.getCurrentTag()
                ParserNative.clearAllResources()

                return@parser HtmlData(
                    elements = elements,
                    headData = HtmlHeadData(
                        title = ParserNative.getTitle(),
                        baseUrl = ParserNative.getBase()
                    ),
                    failure = HtmlData.Failure(
                        message = "Error while processing $tag, original message:\n" + ParserNative.getErrorMessage(),
                        code = ParserNative.getErrorCode(),
                    )
                )
            }

            val data = HtmlData(
                elements = elements,
                headData = HtmlHeadData(
                    title = ParserNative.getTitle(),
                    baseUrl = ParserNative.getBase()
                )
            )
            ParserNative.clearAllResources()
            ProcessorNative.clearAllResources()
            return@parser data
        }
    }


    /**
     * @since 1.0.0
     */
    private suspend fun onElement(elements: MutableList<HtmlElement>) {
        val type = ParserNative.getContentType()
        if (type == HtmlContentType.NO_CONTENT) {
            return
        }
        when (type) {
            HtmlContentType.IMAGE -> {
                var url: String = ParserNative.getContentMapItem(attributeName = "src")

                if (url.endsWith(suffix = ".svg")) {
                    //Svg format not suppor
                    return
                }

                //FIX
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    val base = ParserNative.getBase().removeSuffix(suffix = "/")
                    val end = url.removePrefix(prefix = "/")
                    url = "$base/$end"
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
                        url = url,
                        description = alt,
                        defaultSize = size,
                        alt = alt
                    )
                )
            }

            HtmlContentType.TEXT -> {
                val content = ParserNative.getContent()
                val text = HtmlElement.TextBlock(text = content)
                elements.add(element = text)
            }

            HtmlContentType.TITLE -> {
                elements.add(
                    element = HtmlElement.Title(
                        text = ParserNative.getContent(),
                        titleTag = ParserNative.getCurrentTag()
                    )
                )
            }

            HtmlContentType.LIST -> {
                elements.add(
                    element = HtmlElement.BasicList(
                        isOrdered = ParserNative.getCurrentTag() == "ol",
                        items = ArrayList<String>().apply {
                            val listSize = ParserNative.getContentListSize()
                            for (i in 0 until listSize) {
                                add(ParserNative.getContentListItem(index = i))
                            }
                        }
                    )
                )
            }

            HtmlContentType.QUOTE -> {
                elements.add(element = HtmlElement.Quote(text = ParserNative.getContent()))
            }

            HtmlContentType.CODE -> {
                elements.add(element = HtmlElement.Code(content = ParserNative.getContent()))
            }

            HtmlContentType.TABLE -> {
                val rows = ArrayList<List<String>>()

                val columnCount = ParserNative.getTableColumnCount()
                val rowsCount = ParserNative.getTableRowsCount()

                for (i in 0 until rowsCount) {
                    val columns = ArrayList<String>()
                    for (j in 0 until columnCount) {
                        val el = ParserNative.getTableCell(row = i, column = j)
                        columns.add(element = el)
                    }
                    rows.add(columns)
                }

                elements.add(element = HtmlElement.Table(rows = rows))
            }

            else -> {}
        }
    }
}