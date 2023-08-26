package mir.oslav.jet.html.parse

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mir.oslav.jet.html.HtmlConstants
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.data.IgnoreOptions
import mir.oslav.jet.html.data.Monitoring
import mir.oslav.jet.html.normalizedUrl
import mir.oslav.jet.html.parse.CoreHtmlArticleParser.indexOfSubstring


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
object HtmlArticleParser {


    /**
     * @param content Html content to parse
     * @param ignoreOptions Specifying elements that are going to be ingored in result
     * @return [HtmlData.Success] when [content] was processed and parsed sucessfully, [HtmlData.Invalid]
     * otherwise.
     * @since 1.0.0
     */
    suspend fun parse(
        content: String,
        ignoreOptions: IgnoreOptions = IgnoreOptions(),
        listener: HtmlArticleParserListener = LinearListener()
    ): HtmlData = withContext(context = Dispatchers.IO) {
        return@withContext try {
            parseHtmlArticle(
                content = content,
                ignoreOptions = ignoreOptions,
                listener = listener
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
            HtmlData.Invalid(
                title = "TODO",
                message = "No message",
                exception = exception
            )
        }
    }


    /**
     * @since 1.0.0
     */
    private fun parseHtmlArticle(
        content: String,
        ignoreOptions: IgnoreOptions,
        listener: HtmlArticleParserListener
    ): HtmlData.Success {
        //start time for monitoring
        val startTime = System.currentTimeMillis()

        //preventing duplicates
        val styledMap: HashMap<Pair<Int, Int>, String> = HashMap()
        var index = 0
        var imagesCount = 0

        //helps to determine if we are parsing tag that's inside another one
        var enclosingTag: String? = null

        while (index in content.indices) {
            val char = content[index]

            if (char == '<') {
                val startingTagEndIndex = content.indexOf(
                    char = '>',
                    startIndex = index
                )
                val rawTagWithAttributes = content.substring(
                    startIndex = index + 1,
                    endIndex = startingTagEndIndex
                )
                var tagName = rawTagWithAttributes

                if (tagName.contains(char = ' ')) {
                    tagName = tagName.split(' ').first()
                }

                val isTagIgnored = ignoreOptions.tags.contains(tagName)
                val isKeywordIgnored = ignoreOptions.keywords.contains(tagName)

                //Continue when tag should be ignored
                if (isTagIgnored || isKeywordIgnored) {
                    index += content.indexOf(char = '>', startIndex = index)
                    continue
                }

                val isPairTag = HtmlConstants.pairTags.contains(tagName)
                val isSingeTag = HtmlConstants.singleTags.contains(tagName)

                when {
                    isSingeTag -> {
                        if (tagName == "img") {
                            val rawUrl = rawTagWithAttributes.split("src=")
                            var url = rawUrl
                                .lastOrNull()
                                ?.normalizedUrl()

                            if (url?.contains(' ') == true) {
                                url = url.split(' ').firstOrNull()
                            }

                            url?.let {
                                listener.onImage(
                                    image = HtmlElement.Image(
                                        url = it,
                                        startIndex = index,
                                        endIndex = startingTagEndIndex
                                    )
                                )
                                imagesCount += 1
                            }
                            index = startingTagEndIndex + 1
                            continue
                        }
                    }

                    isPairTag -> {
                        val closingTagStart = content.indexOfSubstring(
                            requestedString = "</$tagName>",
                            fromIndex = index
                        ) ?: continue

                        val firstContentIndex = startingTagEndIndex + 1
                        val lastContentIndex = closingTagStart - 1
                        val tagBody = content.substring(firstContentIndex, closingTagStart)

                        if (tagName == "title") {
                            listener.onTitle(title = tagBody)
                        }

                        if (HtmlConstants.styledTags.contains(tagName)) {
                            styledMap[Pair(startingTagEndIndex + 1, closingTagStart - 1)] = tagBody
                        }

                        if (styledMap.values.contains(tagBody)) {
                            val pair = Pair(startingTagEndIndex + 1, closingTagStart - 1)
                            if (styledMap.keys.contains(pair)) {
                                index = startingTagEndIndex + 1
                                continue
                            }
                        }

                        when (tagName) {
                            "table" -> {
                                listener.onTable(
                                    table = CoreHtmlArticleParser.parseTableFromText(
                                        startIndex = startingTagEndIndex,
                                        endIndex = lastContentIndex,
                                        content = tagBody
                                    )
                                )
                            }

                            "blockquote" -> {
                                listener.onQuote(
                                    quote = HtmlElement.Quote(
                                        text = tagBody,
                                        startIndex = startingTagEndIndex + 1,
                                        endIndex = closingTagStart
                                    )
                                )
                            }

                            else -> {
                                listener.onTextBlock(
                                    textBlock = HtmlElement.TextBlock(
                                        text = tagBody,
                                        startIndex = startingTagEndIndex + 1,
                                        endIndex = closingTagStart
                                    )
                                )
                            }
                        }
                    }
                }

                index = startingTagEndIndex + 1

            } else {
                index += 1
            }
        }

        val monitoring = Monitoring(startTime = startTime, endTime = System.currentTimeMillis())
        val data = listener.onDataRequested(monitoring = monitoring)
        return data
    }
}