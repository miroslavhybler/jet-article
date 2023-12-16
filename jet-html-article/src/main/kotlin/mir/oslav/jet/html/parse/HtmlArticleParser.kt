@file:Suppress("RedundantVisibilityModifier")

package mir.oslav.jet.html.parse

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import mir.oslav.jet.html.data.HtmlConfig
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.data.HtmlHeadData
import mir.oslav.jet.html.data.IgnoreOptions
import mir.oslav.jet.html.data.HtmlParseMetering
import mir.oslav.jet.html.parse.DedicatedTagParser.indexOfSubstring
import mir.oslav.jet.html.parse.listeners.LinearListener
import kotlin.jvm.Throws


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
//TODO refactor
//TODO eliminate using substring() to minimum
//TODO replace kotlin lists functions to increase performance
//TODO </![cdata[>
public object HtmlArticleParser {


    private val tagsCounts: HashMap<String, Int> = HashMap()
    private var totalBodyTagsCount: Int = 0

    private var isDoingMetering: Boolean = false

    /**
     * @param content Html content text to parse
     * @param ignoreOptions Specifying elements that are going to be ignored in result
     * @return [HtmlData]
     * otherwise.
     * @since 1.0.0
     */
    public fun parse(
        content: String,
        listener: HtmlArticleParserListener = LinearListener(),
        ignoreOptions: IgnoreOptions = IgnoreOptions(),
        config: HtmlConfig = HtmlConfig(),
        isDoingMetering: Boolean = false
    ): Flow<HtmlData> {
        tagsCounts.clear()
        totalBodyTagsCount = 0
        this.isDoingMetering = isDoingMetering
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
                value = HtmlData(
                    error = HtmlData.HtmlDataError(
                        message = "No message",
                        cause = exception
                    ),
                    headData = null,
                    elements = emptyList(),
                    loadingStates = HtmlData.LoadingStates(
                        isLoading = false,
                        isAppending = false,
                        message = null
                    )
                )
            )
        }
    }


    //TODO raw content text in any tag, like text in div <div>Text outside "p"</div>
    @Throws(Exception::class)
    private fun parseHtmlArticle(
        content: String,
        ignoreOptions: IgnoreOptions,
        listener: HtmlArticleParserListener,
        config: HtmlConfig,
    ): Flow<HtmlData> = flow {
        emit(
            value = HtmlData(
                error = null,
                elements = emptyList(),
                loadingStates = HtmlData.LoadingStates(
                    isLoading = true,
                    isAppending = true,
                    message = "Started",
                ),
                headData = null
            )
        )

        var index = 0
        var headData: HtmlHeadData? = null
        val startTime: Long? = if (isDoingMetering) System.currentTimeMillis() else null
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

                    emit(
                        value = listener.onDataRequested(
                            config = config,
                            metering = null,
                            headData = headData,
                            loadingStates = HtmlData.LoadingStates(
                                isLoading = true,
                                isAppending = true,
                                message = null
                            )
                        )
                    )
                    index = ceIndex + 7
                    continue
                }

                "body" -> {
                    //Tag content with starting and closing tag <>...</>
                    val ceIndex = content.indexOfSubstring(substring = "</body>", fromIndex = index)
                    //Plus one because startIndex is inclusive and would include '<' char
                    val tagContent = content.substring(startIndex = eIndex + 1, endIndex = ceIndex)
                    index = ceIndex + 7
                    parseBodyTags(
                        content = tagContent,
                        listener = listener,
                        config = config,
                        ignoreOptions = ignoreOptions,
                        upperFlow = this
                    )


                    val metering = if (isDoingMetering && startTime != null) {
                        HtmlParseMetering(
                            startTime = startTime,
                            endTime = System.currentTimeMillis(),
                            tagsCount = totalBodyTagsCount,
                            tags = tagsCounts
                        )
                    } else null

                    emit(
                        value = listener.onDataRequested(
                            config = config,
                            metering = metering,
                            headData = headData,
                            HtmlData.LoadingStates(
                                isLoading = false,
                                isAppending = false,
                                message = null
                            )
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
    @Throws(Exception::class)
    private suspend fun parseBodyTags(
        content: String,
        config: HtmlConfig,
        listener: HtmlArticleParserListener,
        ignoreOptions: IgnoreOptions,
        upperFlow: FlowCollector<HtmlData>
    ) {
        var index = 0

        while (index in content.indices) {
            val char = content[index]

            //TODO text outside tags
            if (char != '<') {
                index += 1
                continue
            }

            //Char is starting tag '<'
            //Actual char is start of tag '<'
            val seIndex = content.indexOf(char = '>', startIndex = index)

            if (index + 1 < content.length) {
                //Checkout ofr invalid closing tag
                val nextChar = content[index + 1]
                if (nextChar == '/') {
                    //Probably some invalid tag, continue
                    index += 1
                    continue
                }
            }

            //Tag body within <...>
            val tagBody = try {
                content.substring(startIndex = index + 1, endIndex = seIndex)
            } catch (ignored: Exception) {
                index = seIndex
                continue
            }

            if (index + 3 < content.length) {
                val substring = content.substring(startIndex = index, endIndex = index + 4)
                if (substring == "<!--") {
                    //Html comment, skipping to the next char after the comment
                    index = content.indexOfSubstring(substring = "-->", fromIndex = index) + 1
                    continue
                }
            }

            val classIndication = "class=\""
            if (tagBody.contains(other = classIndication, ignoreCase = true)) {
                val cis = tagBody.indexOfSubstring(substring = classIndication, fromIndex = 0)
                val cie = tagBody.indexOfSubstring(substring = "\"", fromIndex = cis)
                val tagClassesText = tagBody.substring(startIndex = cis, endIndex = cie)
                val tagClasses = tagClassesText.split(' ')

                if (tagClasses.isNotEmpty() && ignoreOptions.classes.isNotEmpty()) {
                    var skipInMainCycle = false
                    for (tagClass in tagClasses) {
                        if (ignoreOptions.classes.contains(element = tagClass)) {
                            skipInMainCycle = true
                            break
                        }
                    }

                    if (skipInMainCycle) {
                        index = seIndex
                        continue
                    }
                }
            }

            val tag = extractTagName(tagBody = tagBody)

            if (isDoingMetering) {
                if (tagsCounts.containsKey(key = tag)) {
                    val value = tagsCounts[tag] ?: 0
                    tagsCounts[tag] = value + 1
                } else {
                    tagsCounts[tag] = 1
                }
                totalBodyTagsCount += 1
            }

            when (tag) {
                "img" -> {
                    DedicatedTagParser.parseImageFromText(
                        startIndex = index,
                        endIndex = seIndex,
                        config = config,
                        rawTagWithAttributes = tagBody
                    )?.let(listener::onImage)

                    index = seIndex + 1
                    continue
                }
                //Pair tags

                else -> {
                    val closingTag = "</$tag>"
                    val cleIndex = content.indexOfSubstring(
                        substring = closingTag,
                        fromIndex = seIndex
                    )

//                    if (ignoreOptions.tags.contains(element = tag)) {
//                        Log.d("mirek", "ignoring tag: $tag")
//                        index = cleIndex
//                        continue
//                    }

                    if (tagBody.contains(other = classIndication, ignoreCase = true)) {
                        val cis = tagBody.indexOfSubstring(
                            substring = classIndication,
                            fromIndex = 0
                        )
                        val cie = tagBody.indexOfSubstring(
                            substring = "\"",
                            fromIndex = cis + classIndication.length
                        )
                        val tagClassesText = tagBody.substring(
                            startIndex = cis + classIndication.length,
                            endIndex = cie
                        )
                        val tagClasses = tagClassesText
                            .removePrefix(prefix = "\"")
                            .removeSuffix(suffix = "\"")
                            .split(' ')
                            .filter { tagClass -> tagClass != "" }
                            .mapAsync { tagClass -> tagClass.trim() }

                        if (tagClasses.isNotEmpty() && ignoreOptions.classes.isNotEmpty()) {
                            var skipInMainCycle = false
                            for (tagClass in tagClasses) {
                                if (ignoreOptions.classes.contains(element = tagClass)) {
                                    Log.d("mirek", "ignoring class: $tagClass")
                                    skipInMainCycle = true
                                    break
                                }
                            }

                            if (skipInMainCycle) {
                                index = cleIndex
                                continue
                            }
                        }
                    }

                    //Plus one because startIndex is inclusive and would include '<' char
                    //TODO dont work for <div><div></div>
                    val tagContent = try {
                        content.substring(
                            startIndex = seIndex + 1,
                            endIndex = cleIndex
                        )
                    } catch (exception: Exception) {
                        Log.e(
                            "mirek",
                            "bug for $tag with closing $closingTag from contet:\n\n$content\n\n"
                        )
                        return
                    }

                    processPairTag(
                        tag = tag,
                        listener = listener,
                        config = config,
                        content = tagContent,
                        startContentIndex = seIndex,
                        endContentIndex = cleIndex,
                        upperFlow = upperFlow,
                        ignoreOptions = ignoreOptions
                    )

                    index = cleIndex + closingTag.length + 1
                }
            }
        }
    }

    @Throws(Exception::class)
    private suspend fun processPairTag(
        tag: String,
        content: String,
        listener: HtmlArticleParserListener,
        config: HtmlConfig,
        startContentIndex: Int,
        endContentIndex: Int,
        upperFlow: FlowCollector<HtmlData>,
        ignoreOptions: IgnoreOptions,
    ) {

        var canEmitNewTag = false
        //Support pre
        when (tag) {
            "address" -> {
                canEmitNewTag = true
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
                canEmitNewTag = true
                listener.onTable(
                    table = DedicatedTagParser.parseTableFromText(
                        startIndex = startContentIndex,
                        endIndex = endContentIndex,
                        content = content,
                        config = config
                    )
                )
            }

            "ol", "ul" -> {
                canEmitNewTag = true
                listener.onBasicList(
                    basicList = DedicatedTagParser.parseBasicListFromText(
                        isOrdered = tag == "ol",
                        content = content,
                        startIndex = startContentIndex,
                        endIndex = endContentIndex,
                        config = config
                    )
                )
            }

            "blockquote" -> {
                canEmitNewTag = true
                listener.onQuote(
                    quote = HtmlElement.Parsed.Quote(
                        text = content,
                        startIndex = startContentIndex,
                        endIndex = startContentIndex,
                        span = config.spanCount
                    )
                )
            }

            "code" -> {
                canEmitNewTag = true
                listener.onCode(
                    code = HtmlElement.Parsed.Code(
                        content = content,
                        startIndex = startContentIndex,
                        endIndex = endContentIndex,
                        span = config.spanCount
                    )
                )
            }

            "h1", "h2", "h3", "h4", "h5", "h6", "h7" -> {
                canEmitNewTag = true
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

            "p" -> {
                canEmitNewTag = true
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

            else -> {
                //Other tags like div, span, section are consider to be a "wrapper" tags, trying
                //To parse content from these tags
                //TODO nebere v potaz vnořenost <div><div></div></div>
                //TODO vyletí protože spojí první <div> a </div>
                parseBodyTags(
                    content = content,
                    config = config,
                    listener = listener,
                    ignoreOptions = ignoreOptions,
                    upperFlow = upperFlow
                )
            }
        }


        if (canEmitNewTag) {
            upperFlow.emit(
                value = listener.onDataRequested(
                    config = config,
                    metering = null,
                    headData = null,
                    HtmlData.LoadingStates(
                        isLoading = true,
                        isAppending = true,
                        message = null
                    )
                )
            )
        }
    }


    @Throws(Exception::class)
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

            if (tag == "title" || tag == "base") {

                val tagContent = try {
                    content.substring(
                        startIndex = seIndex + 1,
                        endIndex = clIndex
                    )
                } catch (ignored: Exception) {
                    index = clIndex.takeIf { it != -1 } ?: (seIndex + 1)
                    continue
                }
                when (tag) {
                    "title" -> title = tagContent
                    "base" -> base = tagContent
                }
                index = clIndex + closingTag.length + 1
            } else {
                index = clIndex
                continue
            }
        }
        return HtmlHeadData(title = title, baseUrl = base)
    }


    /**
     * @return
     */
    private fun extractTagName(tagBody: String): String {
        val rawTagName = if (tagBody.contains(char = ' ')) {
            val tagEIndex = tagBody.indexOfFirst { tagChar -> tagChar == ' ' }
            tagBody.substring(startIndex = 0, endIndex = tagEIndex)
        } else tagBody

        return rawTagName.trim().lowercase()
    }


    private suspend fun <T, R> List<T>.mapAsync(
        mapper: suspend (T) -> R
    ): List<R> = coroutineScope { map { async { mapper(it) } }.awaitAll() }
}