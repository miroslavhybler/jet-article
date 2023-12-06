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
                    title = "TODO",
                    message = "No message",
                    exception = exception
                )
            )
        }
    }


    /**
     * @since 1.0.0
     */
    //TODO needs complete refactor
    private fun parseHtmlArticle(
        content: String,
        ignoreOptions: IgnoreOptions,
        listener: HtmlArticleParserListener,
        config: HtmlConfig,
    ): Flow<HtmlData> = flow {
        //start time for monitoring
        val startTime = System.currentTimeMillis()
        val tagDurations: ArrayList<Long> = ArrayList()

        emit(value = HtmlData.Loading(message = "Loading core"))

        //preventing duplicates
        val styledMap: HashMap<Pair<Int, Int>, String> = HashMap()
        var index = 0
        var totalTags = 0
        var ignoredTags = 0
        var usedTags = 0
        var tagStartTime = 0L
        //holds the raw content that is not wrapped in tags
        var rawContentText: String = ""
        var rawContentStartIndex: Int = 0

        //helps to determine if we are parsing tag that's inside another one
        var enclosingTag: String? = null

        while (index in content.indices) {
            val char = content[index]

            if (char == '<') {

                /*
                if (rawContentText.isNotBlank()) {
                    listener.onTextBlock(
                        textBlock = HtmlElement.Parsed.TextBlock(
                            text = rawContentText,
                            startIndex = rawContentStartIndex,
                            endIndex = index,
                            span = config.spanCount
                        )
                    )
                    rawContentText = ""
                }
                 */

                tagStartTime = System.currentTimeMillis()
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
                    ignoredTags += 1
                    totalTags += 1
                    continue
                }

                val isPairTag = HtmlConstants.pairTags.contains(tagName)
                val isSingeTag = HtmlConstants.singleTags.contains(tagName)

                when {
                    isSingeTag -> {
                        if (tagName == "img") {
                            CoreHtmlArticleParser.parseImageFromText(
                                startIndex = index,
                                endIndex = startingTagEndIndex,
                                config = config,
                                rawTagWithAttributes = rawTagWithAttributes
                            )?.let(listener::onImage)

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
                            "address" -> {
                                listener.onAddress(
                                    HtmlElement.Parsed.Address(
                                        startIndex = startingTagEndIndex,
                                        endIndex = lastContentIndex,
                                        span = config.spanCount,
                                        content = tagBody
                                    )
                                )
                            }

                            "table" -> {
                                listener.onTable(
                                    table = CoreHtmlArticleParser.parseTableFromText(
                                        startIndex = startingTagEndIndex,
                                        endIndex = lastContentIndex,
                                        content = tagBody,
                                        config = config
                                    )
                                )
                            }

                            "blockquote" -> {
                                listener.onQuote(
                                    quote = HtmlElement.Parsed.Quote(
                                        text = tagBody,
                                        startIndex = startingTagEndIndex + 1,
                                        endIndex = closingTagStart,
                                        span = config.spanCount
                                    )
                                )
                            }

                            "h1", "h2", "h3", "h4", "h5", "h6", "h7" -> {
                                listener.onTitle(
                                    title = HtmlElement.Parsed.Title(
                                        text = tagBody,
                                        startIndex = startingTagEndIndex + 1,
                                        endIndex = closingTagStart,
                                        span = config.spanCount,
                                        titleTag = tagName
                                    )
                                )
                            }

                            else -> {
                                //Handles text elements like p, h1, h2, ...
                                listener.onTextBlock(
                                    textBlock = HtmlElement.Parsed.TextBlock(
                                        text = tagBody,
                                        startIndex = startingTagEndIndex + 1,
                                        endIndex = closingTagStart,
                                        span = config.spanCount
                                    )
                                )
                            }
                        }
                    }
                }
                usedTags += 1
                totalTags += 1
                index = startingTagEndIndex + 1
                tagDurations.add(System.currentTimeMillis() - tagStartTime)
            } else {
                //rawContentText += char

                //    if (rawContentStartIndex == 0) {
                //        rawContentStartIndex = index
                //    }

                index += 1
            }
        }

        val monitoring = ParseMetrics(
            startTime = startTime,
            endTime = System.currentTimeMillis(),
            ignoredTags = ignoredTags,
            usedTags = usedTags,
            totalTags = totalTags,
            averageDurationPerTag = tagDurations.average()
        )
        emit(
            value = listener.onDataRequested(
                config = config,
                monitoring = monitoring
            )
        )
    }
}