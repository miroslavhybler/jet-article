package mir.oslav.jet.html.article

import android.util.Log
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import mir.oslav.jet.html.article.data.HtmlContentType
import mir.oslav.jet.html.article.data.HtmlData
import mir.oslav.jet.html.article.data.HtmlElement
import mir.oslav.jet.html.article.data.HtmlHeadData
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 03.01.2024
 */
object JetHtmlArticleParser {


    private val safeCoroutineContext: CoroutineContext = Dispatchers.Default +
            CoroutineName(name = "JetHtmlArticleParse")


    private val elementsScope: CoroutineScope = CoroutineScope(
        context = Executors.newFixedThreadPool(1).asCoroutineDispatcher() +
                CoroutineName(name = "elementsScope")
    )


    fun parse(content: String): Flow<HtmlData> = flow {
        val elements = ArrayList<HtmlElement>()
        ContentParserNative.setContent(content = content)

        if (!ContentParserNative.hasNextStep()) {
            emit(value = errorData())
            return@flow
        }

        while (ContentParserNative.hasNextStep()) {
            ContentParserNative.doNextStep()

            if (ContentParserNative.hasContent()) {
                val type = ContentParserNative.getContentType()
                val c = ContentParserNative.getContent()

                if (type == HtmlContentType.NO_CONTENT) {
                    continue
                }
                Log.d("mirek", "Type: $type")
                when (type) {
                    HtmlContentType.PARAGRAPH -> {
                        elements.add(element = HtmlElement.TextBlock(text = c))
                    }
                    HtmlContentType.TITLE -> {
                        elements.add(element = HtmlElement.Title(text = c, titleTag = "h3"))
                    }
                    else -> {}
                }

                // emit(value = appendingData(elements = elements))
            }
        }

        Log.d("mirek", "elements atEnd: ${elements.size}")
        emit(value = finalData(elements = elements))
        ContentParserNative.clearAllResources()
        return@flow
    }.flowOn(context = safeCoroutineContext)


    private fun appendingData(
        elements: List<HtmlElement>
    ): HtmlData = HtmlData(
        loadingStates = HtmlData.LoadingStates.appending,
        elements = elements,
        isFullyLoaded = false,
        error = null,
        headData = HtmlHeadData(title = "Parsing", baseUrl = null)
    )


    private fun finalData(
        elements: List<HtmlElement>,
    ): HtmlData = HtmlData(
        loadingStates = HtmlData.LoadingStates(
            isLoading = false,
            isAppending = false,
            message = null
        ),
        elements = elements,
        isFullyLoaded = true,
        error = null,
        headData = HtmlHeadData(title = "Parsed successfully", baseUrl = null)
    )


    private fun errorData(
        message: String = "",
        cause: Throwable? = null
    ): HtmlData = HtmlData(
        loadingStates = HtmlData.LoadingStates(
            isLoading = false,
            isAppending = false,
            message = null
        ),
        elements = emptyList(),
        isFullyLoaded = true,
        error = HtmlData.HtmlDataError(message = message, cause = cause),
        headData = null
    )
}