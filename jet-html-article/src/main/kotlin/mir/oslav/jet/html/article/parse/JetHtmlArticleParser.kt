package mir.oslav.jet.html.article.parse

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mir.oslav.jet.html.article.data.HtmlData
import mir.oslav.jet.html.article.data.HtmlElement
import mir.oslav.jet.html.article.data.HtmlHeadData


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 03.01.2024
 */
object JetHtmlArticleParser {

    fun parse(content: String): Flow<HtmlData> = flow {
        val elements = ArrayList<HtmlElement>()
        ContentParserNative.setContent(content = content)

        if (!ContentParserNative.hasNextStep()) {
            emit(
                value = HtmlData(
                    loadingStates = HtmlData.LoadingStates(
                        isLoading = false,
                        isAppending = false,
                        message = null
                    ),
                    elements = elements,
                    metering = null,
                    isFullyLoaded = true,
                    error = HtmlData.HtmlDataError(message = "TODO", cause = Throwable()),
                    headData = null
                )
            )
            return@flow
        }

        while (ContentParserNative.hasNextStep()) {
            ContentParserNative.doNextStep()
            if (ContentParserNative.hasContent()) {
                val c = ContentParserNative.getContent()
                elements.add(element = HtmlElement.TextBlock(text = c,))

                emit(
                    value = HtmlData(
                        loadingStates = HtmlData.LoadingStates(
                            isLoading = true,
                            isAppending = true,
                            message = null
                        ),
                        elements = elements,
                        metering = null,
                        isFullyLoaded = false,
                        error = null,
                        headData = HtmlHeadData(title = "Parsing", baseUrl = null)
                    )
                )
            }
        }

        emit(
            value = HtmlData(
                loadingStates = HtmlData.LoadingStates(
                    isLoading = false,
                    isAppending = false,
                    message = null
                ),
                elements = elements,
                metering = null,
                isFullyLoaded = true,
                error = null,
                headData = HtmlHeadData(title = "Parsed successfully", baseUrl = null)
            )
        )
        return@flow
    }
}