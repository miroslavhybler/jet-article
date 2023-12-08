package mir.oslav.jet.html.parse

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import mir.oslav.jet.html.HtmlConstants
import mir.oslav.jet.html.data.HtmlConfig
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.data.HtmlHeadData
import mir.oslav.jet.html.data.IgnoreOptions
import mir.oslav.jet.html.data.ParseMetrics
import mir.oslav.jet.html.parse.CoreHtmlArticleParser.indexOfSubstring
import mir.oslav.jet.html.parse.listeners.GalleryGroupingListener


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
//TODO refactor
//TODO eliminate using substring() to minimum
object HtmlArticleParser {


    /**
     * @param content Html content text to parse
     * @param ignoreOptions Specifying elements that are going to be ingored in result
     * @return [HtmlData.Success] when [content] was processed and parsed sucessfully, [HtmlData.Invalid]
     * otherwise.
     * @since 1.0.0
     */
    fun parse(
        content: String,
        listener: HtmlArticleParserListener = GalleryGroupingListener(),
        ignoreOptions: IgnoreOptions = IgnoreOptions(),
        config: HtmlConfig = HtmlConfig(),
    ): Flow<HtmlData> {
        return try {
            parseHtmlArticle(
                content = content,
                ignoreOptions = ignoreOptions,
                listener = listener,
                config = config
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
            flowOf(
                value = HtmlData.Invalid(
                    message = "No message",
                    exception = exception
                )
            )
        }
    }


    //TODO add metrics
    private fun parseHtmlArticle(
        content: String,
        ignoreOptions: IgnoreOptions,
        listener: HtmlArticleParserListener,
        config: HtmlConfig,
    ): Flow<HtmlData> = flow {
        emit(value = HtmlData.Loading(message = "TODO started"))

        var index = 0
        var headData: HtmlHeadData? = null

        while (index in content.indices) {
            val char = content[index]

            if (char != '<') {
                index += 1
                continue
            }

            //Comment check
            if (index + 3 < content.length) {
                val substring = content.substring(startIndex = index, endIndex = index + 3)
                if (substring == "<!--") {
                    //Html comment, skipping to the next char after the comment
                    index = content.indexOfSubstring(substring = "-->", fromIndex = index) + 1
                    continue
                }
            }

            //Actual char is start of tag '<'
            val eIndex = content.indexOf(char = '>', startIndex = index)
            //Tag body within <...>
            val tagBody = content.substring(startIndex = index + 1, endIndex = eIndex)

            when (val tag = extractTagName(tagBody = tagBody)) {
                "head" -> {
                    //Tag content with starting and closing tag <>...</>
                    val ceIndex = content.indexOfSubstring(substring = "</head>", fromIndex = index)
                    //Plus one because startIndex is inclusive and would include '<' char
                    val tagContent = content.substring(startIndex = eIndex + 1, endIndex = ceIndex)
                    headData = parseHeadData(content = tagContent)
                    index = ceIndex + 7
                    continue
                }

                "body" -> {
                    //Tag content with starting and closing tag <>...</>
                    val ceIndex = content.indexOfSubstring(substring = "</body>", fromIndex = index)
                    //Plus one because startIndex is inclusive and would include '<' char
                    val tagContent = content.substring(startIndex = eIndex + 1, endIndex = ceIndex)
                    index = ceIndex + 7
                    parseBody(
                        content = tagContent,
                        listener = listener,
                        config = config
                    )

                    emit(
                        value = listener.onDataRequested(
                            config = config,
                            monitoring = ParseMetrics(
                                0, 0,
                                0.0,
                                0,
                                0,
                                0
                            ),
                            headData = headData
                        )
                    )
                    return@flow
                }

                else -> {
                    index = eIndex + 1
                    continue
                }
            }
        }
    }.flowOn(context = Dispatchers.Default)


    /**
     * @since 1.0.0
     */
    //TODO this is old function
    private fun parseBody(
        content: String,
        config: HtmlConfig,
        listener: HtmlArticleParserListener
    ) {
        var index = 0

        while (index in content.indices) {
            val char = content[index]

            if (char != '<') {
                index += 1
                continue
            }

            //Char is starting tag '<'
            //Actual char is start of tag '<'
            val seIndex = content.indexOf(char = '>', startIndex = index)
            //Tag body within <...>
            val tagBody = content.substring(startIndex = index + 1, endIndex = seIndex)

            if (index + 3 < content.length) {
                val substring = content.substring(startIndex = index, endIndex = index + 4)
                if (substring == "<!--") {
                    //Html comment, skipping to the next char after the comment
                    index = content.indexOfSubstring(substring = "-->", fromIndex = index) + 1
                    continue
                }
            }

            when (val tag = extractTagName(tagBody = tagBody)) {
                "img" -> {
                    CoreHtmlArticleParser.parseImageFromText(
                        startIndex = index,
                        endIndex = seIndex,
                        config = config,
                        rawTagWithAttributes = tagBody
                    )?.let(listener::onImage)

                    index = seIndex + 1
                    continue
                }

                else -> {
                    //TODO /p
                    //TODO padá protože hledá //p
                    //TODO proč to hledá "/img"
                    val closingTag = "</$tag>"
                    val cleIndex = content.indexOfSubstring(
                        substring = closingTag,
                        fromIndex = seIndex
                    )

                    //Plus one because startIndex is inclusive and would include '<' char
                    val tagContent = try {
                        content.substring(
                            startIndex = seIndex + 1,
                            endIndex = cleIndex
                        )
                    } catch (exception: Exception) {
                        Log.e("mirek", "bug for $tag with closing $closingTag")
                        throw exception
                    }

                    processPairTag(
                        tag = tag,
                        listener = listener,
                        config = config,
                        content = tagContent,
                        startContentIndex = seIndex,
                        endContentIndex = cleIndex
                    )

                    index = cleIndex + closingTag.length + 1
                }
            }
        }
    }


    private fun processPairTag(
        tag: String,
        content: String,
        listener: HtmlArticleParserListener,
        config: HtmlConfig,
        startContentIndex: Int,
        endContentIndex: Int,
    ) {
        when (tag) {
            "address" -> {
                listener.onAddress(
                    HtmlElement.Parsed.Address(
                        startIndex = startContentIndex,
                        endIndex = endContentIndex,
                        span = config.spanCount,
                        content = content
                    )
                )
            }

            "table" -> {
                listener.onTable(
                    table = CoreHtmlArticleParser.parseTableFromText(
                        startIndex = startContentIndex,
                        endIndex = endContentIndex,
                        content = content,
                        config = config
                    )
                )
            }

            "blockquote" -> {
                listener.onQuote(
                    quote = HtmlElement.Parsed.Quote(
                        text = content,
                        startIndex = startContentIndex + 1,
                        endIndex = startContentIndex,
                        span = config.spanCount
                    )
                )
            }

            "h1", "h2", "h3", "h4", "h5", "h6", "h7" -> {
                listener.onTitle(
                    title = HtmlElement.Parsed.Title(
                        text = content,
                        startIndex = endContentIndex + 1,
                        endIndex = startContentIndex,
                        span = config.spanCount,
                        titleTag = tag
                    )
                )
            }

            else -> {
                //paragraph
                listener.onTextBlock(
                    textBlock = HtmlElement.Parsed.TextBlock(
                        text = content,
                        startIndex = startContentIndex + 1,
                        endIndex = endContentIndex,
                        span = config.spanCount
                    )
                )
            }
        }
    }


    private fun parseHeadData(
        content: String
    ): HtmlHeadData {

        var title: String? = null
        var base: String? = null
        var index = 0

        while (index in content.indices) {
            val char = content[index]
            if (char != '<') {
                index += 1
                continue
            }

            //Char is starting tag '<'
            //Actual char is start of tag '<'
            val seIndex = content.indexOf(char = '>', startIndex = index)
            //Tag body within <...>
            if (index + 3 < content.length) {
                val substring = content.substring(startIndex = index, endIndex = index + 4)
                if (substring == "<!--") {
                    //Html comment, skipping to the next char after the comment
                    index = content.indexOfSubstring(substring = "-->", fromIndex = index) + 1
                    continue
                }
            }

            val tagBody = content.substring(startIndex = index + 1, endIndex = seIndex)
            val tag = extractTagName(tagBody = tagBody)
            val closingTag = "</$tag>"
            val clIndex = content.indexOfSubstring(substring = closingTag, fromIndex = index)
            val tagContent = content.substring(
                startIndex = index,
                endIndex = clIndex
            )
            when (tag) {
                "title" -> title = tagContent
                "base" -> base = tagContent
            }
            index = clIndex + closingTag.length + 1

        }
        return HtmlHeadData(title = title, baseUrl = base)
    }


    private fun extractTagName(tagBody: String): String {
        return if (tagBody.contains(char = ' ')) {
            val tagEIndex = tagBody.indexOfFirst { tagChar -> tagChar == ' ' }
            tagBody.substring(startIndex = 0, endIndex = tagEIndex)
        } else tagBody
    }
}