package mir.oslav.jet.html.article

import android.content.Context
import android.util.Log
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
                return@parser HtmlData.Failure(message = "Empty", cause = null)
            }

            while (ParserNative.hasNextStep()) {
                ParserNative.doNextStep()
                if (ParserNative.hasContent()) {
                    onElement(elements = elements)
                    ParserNative.resetCurrentContent()
                }
            }
            val data = HtmlData.Success(
                elements = elements,
                headData = HtmlHeadData(
                    title = ParserNative.getTitle(),
                    baseUrl = ParserNative.getBase()
                )
            )
            ParserNative.clearAllResources()
            return@parser data
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
        when (type) {
            HtmlContentType.IMAGE -> {
                val url: String = ParserNative.getContentMapItem(attributeName = "src")
                val alt: String = ParserNative.getContentMapItem(attributeName = "alt")
                elements.add(element = HtmlElement.Image(url = url, description = alt))
            }

            HtmlContentType.TEXT -> {
                elements.add(
                    element = HtmlElement.TextBlock(
                        styledText = ParserNative.getContent(),
                        cleanText = "TODO"
                    )
                )
            }

            HtmlContentType.TITLE -> {
                elements.add(
                    element = HtmlElement.Title(
                        text = ParserNative.getContent(),
                        titleTag = "h3"
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
                elements.add(element = HtmlElement.Table(rows = ArrayList<String>().apply {
                    val listSize = ParserNative.getContentListSize()
                    for (i in 0 until listSize) {
                        add(ParserNative.getContentListItem(index = i))
                    }
                }));
            }

            else -> {}
        }
    }
}