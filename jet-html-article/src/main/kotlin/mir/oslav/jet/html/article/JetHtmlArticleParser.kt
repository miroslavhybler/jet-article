package mir.oslav.jet.html.article

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mir.oslav.jet.html.article.data.HtmlContentType
import mir.oslav.jet.html.article.data.HtmlData
import mir.oslav.jet.html.article.data.HtmlElement
import mir.oslav.jet.html.article.data.HtmlHeadData
import kotlin.coroutines.CoroutineContext


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 03.01.2024
 */
object JetHtmlArticleParser {


    /**
     * @since 1.0.0
     */
    private val safeCoroutineContext: CoroutineContext = Dispatchers.Default
        .plus(context = CoroutineName(name = "JetHtmlArticleParse"))


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
                return@parser errorData(message = "Empty")
            }

            while (ParserNative.hasNextStep()) {
                ParserNative.doNextStep()
                if (ParserNative.hasContent()) {
                    onElement(elements = elements)
                    ParserNative.resetCurrentContent()
                }
            }

            ParserNative.clearAllResources()
            return@parser finalData(elements = elements)
        }
    }


    /**
     * @since 1.0.0
     */
    private fun onElement(elements: MutableList<HtmlElement>) {
        val type = ParserNative.getContentType()
        if (type == HtmlContentType.NO_CONTENT) {
            return
        }
        val c = ParserNative.getContent()
        when (type) {
            HtmlContentType.IMAGE -> {
                val url: String = ParserNative.getContentMapItem(attributeName = "src")
                val alt: String = ParserNative.getContentMapItem(attributeName = "alt")
                elements.add(element = HtmlElement.Image(url = url, description = alt))
            }

            HtmlContentType.PARAGRAPH -> {
                elements.add(
                    element = HtmlElement.TextBlock(
                        styledText = c,
                        cleanText = "TODO"
                    )
                )
            }

            HtmlContentType.TITLE -> {
                elements.add(element = HtmlElement.Title(text = c, titleTag = "h3"))
            }

            HtmlContentType.LIST -> {
                elements.add(
                    element = HtmlElement.BasicList(
                        isOrdered = false,
                        items = ArrayList<String>().apply {
                            for (i in 0 until ParserNative.getContentListSize()) {
                                add(ParserNative.getContentListItem(index = i))
                            }
                        }
                    )
                )
            }

            HtmlContentType.QUOTE -> {
                elements.add(element = HtmlElement.Quote(text = ParserNative.getContent()))
            }

            else -> {}
        }
    }


    private fun appendingData(
        elements: List<HtmlElement>
    ): HtmlData = HtmlData(
        elements = elements,
        error = null,
        headData = HtmlHeadData(title = "Parsing", baseUrl = null)
    )


    private fun finalData(
        elements: List<HtmlElement>,
    ): HtmlData = HtmlData(
        elements = elements,
        error = null,
        headData = HtmlHeadData(title = "Parsed successfully", baseUrl = null)
    )


    private fun errorData(
        message: String = "Error",
        cause: Throwable? = null
    ): HtmlData = HtmlData(
        elements = emptyList(),
        error = HtmlData.HtmlDataError(message = message, cause = cause),
        headData = null
    )
}