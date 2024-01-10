@file:Suppress("RedundantVisibilityModifier", "SpellCheckingInspection")

package mir.oslav.jet.html

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
import mir.oslav.jet.html.article.data.HtmlData
import mir.oslav.jet.html.article.data.HtmlHeadData
import mir.oslav.jet.html.article.iOf
import mir.oslav.jet.html.Parser.indexOfSub
import mir.oslav.jet.html.listeners.LinearListenerOld
import mir.oslav.jet.html.article.sub
import kotlin.jvm.Throws


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
//TODO refactor
//TODO eliminate using substring() to minimum
//TODO replace kotlin lists functions to increase performance
@Deprecated(message = "Use the new one")
public object JetHtmlArticleParserOld {


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
        listener: HtmlArticleParserListener = LinearListenerOld(),
        ignoreOptions: IgnoreOptions = IgnoreOptions(),
        isDoingMetering: Boolean = false
    ): Flow<HtmlData> {
        tagsCounts.clear()
        totalBodyTagsCount = 0
        JetHtmlArticleParserOld.isDoingMetering = isDoingMetering
        return try {
            parseHtmlArticle(
                content = content,
                ignoreOptions = ignoreOptions,
                listener = listener,
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
                        message = null,
                    ),
                    isFullyLoaded = true
                )
            )
        }
    }


    @Throws(Exception::class)
    private fun parseHtmlArticle(
        content: String,
        ignoreOptions: IgnoreOptions,
        listener: HtmlArticleParserListener,
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
                headData = null,
                isFullyLoaded = false
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
                val substring = content.sub(s = index, e = index + 3)
                if (substring == "<!--") {
                    //Html comment, skipping to the next char after the comment
                    index = content.indexOf(string = "-->", startIndex = index) + 1
                    continue
                }
            }

            //Doctype check
            if (index + 15 < content.length) {
                val substring = content.sub(s = index, e = index + 15)
                val doctypeSub = "<!doctype html>"
                if (substring.equals(other = doctypeSub, ignoreCase = true)) {
                    //Plus one to skip to next char after >
                    index += doctypeSub.length + 1
                    continue
                }
            }

            //Actual char is start of tag '<'
            //Start tag end index
            val stei = content.iOf(char = '>', startIndex = index)
            //TagType body within <...>
            val tagBody = content.sub(s = index + 1, e = stei)
            //TagType name lowercase
            when (Parser.extractTagName(tagBody = tagBody)) {
                "head" -> {
                    //TagType content with starting and closing tag <>...</>
                    val ceIndex = content.indexOfSub(substring = "</head>", startIndex = index)
                    //Plus one because startIndex is inclusive and would include '<' char
                    val tagContent = content.sub(s = stei + 1, e = ceIndex)
                    headData = parseHeadData(content = tagContent)

                    emit(
                        value = listener.onDataRequested(
                            headData = headData,
                            loadingStates = HtmlData.LoadingStates(
                                isLoading = true,
                                isAppending = true,
                                message = null
                            ),
                            isFullyLoaded = false
                        )
                    )
                    index = ceIndex + 7
                    continue
                }

                "body" -> {
                    //TagType content with starting and closing tag <>...</>
                    val ceIndex =
                        content.indexOfSub(substring = "</body>", startIndex = index)
                    //Plus one because startIndex is inclusive and would include '<' char
                    index = ceIndex + 7
                    parseBodyContainerTags(
                        content = content,
                        listener = listener,
                        ignoreOptions = ignoreOptions,
                        upperFlow = this,
                        fromIndex = stei + 1,
                        headData = headData
                    )

                    emit(
                        value = listener.onDataRequested(
                            headData = headData,
                            HtmlData.LoadingStates(
                                isLoading = false,
                                isAppending = false,
                                message = null,
                            ),
                            isFullyLoaded = true
                        )
                    )
                    return@flow
                }

                else -> {
                    index = stei + 1
                    continue
                }
            }
        }
    }.flowOn(context = Dispatchers.Default)


    /**
     * @since 1.0.0
     */
    @Throws(Exception::class)
    private suspend fun parseBodyContainerTags(
        content: String,
        listener: HtmlArticleParserListener,
        ignoreOptions: IgnoreOptions,
        upperFlow: FlowCollector<HtmlData>,
        fromIndex: Int,
        headData: HtmlHeadData?,
    ) {
        var index = fromIndex

        while (index in content.indices) {
            val char = content[index]

            //TODO text outside tags
            if (char != '<') {
                index += 1
                continue
            }

            //Actual char is start of tag '<'
            //start tag ending index
            val steIndex = content.iOf(char = '>', startIndex = index)

            if (index + 1 < content.length) {
                //Checkout for invalid closing tag
                val nextChar = content[index + 1]
                if (nextChar == '/') {
                    //Probably some invalid tag, continue
                    index += 1
                    continue
                }
            }

            //TagType body within <...>
            val tagBody = try {
                content.sub(s = index + 1, e = steIndex)
            } catch (ignored: Exception) {
                index = steIndex
                continue
            }

            //Comment check
            if (index + 3 < content.length) {
                val substring = content.sub(s = index, e = index + 4)
                if (substring == "<!--") {
                    //Html comment, skipping to the next char after the comment
                    index = content.indexOfSub(substring = "-->", startIndex = index) + 1
                    continue
                }
            }

            //Doctype check
            //TODO mapbox docs, z nějakého důvodu se po rozparsování body vrací na začátek dokumentu a tam to vyletí na <!doctype html>
            if (index + 15 < content.length) {
                val sub = content.sub(s = index, e = index + 15)
                val doctypeSub = "<!doctype html>"
                if (sub.equals(other = doctypeSub, ignoreCase = true)) {
                    //Plus one to skip to next char after >
                    index += doctypeSub.length + 1
                    continue
                }
            }

            val classIndication = "class=\""
            if (tagBody.contains(other = classIndication, ignoreCase = true)) {
                val cis = tagBody.indexOfSub(substring = classIndication, startIndex = 0)
                val cie = tagBody.indexOfSub(substring = "\"", startIndex = cis)
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
                        index = steIndex
                        continue
                    }
                }
            }

            val tag = Parser.extractTagName(tagBody = tagBody)

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
                "!doctype", "source", "input" -> {
                    //TODO solve better
                    index += tag.length + 1
                    continue
                }

                "img" -> {
                    Parser.parseImageFromText(
                        startIndex = index,
                        endIndex = steIndex,
                        rawTagWithAttributes = tagBody,
                        headData = null
                    )?.let(listener::onImage)

                    index = steIndex + 1
                    Log.d("mirek" , "image")
                    continue
                }
                //Pair tags

                else -> {
                    val closingTag = "</$tag>"
                    //Closing tag end index
                    val ctei = Parser.findClosing(content = content, tag = tag, startIndex = index)
                    //last content index
                    val cli = ctei - closingTag.length

                    if (cli <= steIndex + 1) {
                        //Last contnent index is smaller that staring tag closing index
                        //There is some weird bug, skipping

                        index = steIndex + 1
                        continue
                    }

                    if (tag == "script" || tag == "noScript" || tag == "svg") {
                        index = ctei + 1
                        continue
                    }
//TODO
//                    if (ignoreOptions.tags.contains(element = tag)) {
//                        Log.d("mirek", "ignoring tag: $tag")
//                        index = cleIndex
//                        continue
//                    }

                    if (tagBody.contains(other = classIndication, ignoreCase = true)) {
                        val cis = tagBody.indexOfSub(
                            substring = classIndication,
                            startIndex = 0
                        )
                        val cie = tagBody.indexOfSub(
                            substring = "\"",
                            startIndex = cis + classIndication.length
                        )
                        val tagClassesText = tagBody.substring(
                            startIndex = cis + classIndication.length,
                            endIndex = cie
                        )
                        val tagClasses = tagClassesText
                            .removePrefix(prefix = "\"")
                            .removeSuffix(suffix = "\"")
                            .split(' ')
                            .filter(String::isNotEmpty)
                            .mapAsync(String::trim)

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
                                index = ctei + 1
                                continue
                            }
                        }
                    }
                    val tagContent = try {
                        //Plus one because startIndex is inclusive and would include '<' char
                        content.sub(s = steIndex + 1, e = cli)
                    } catch (exception: Exception) {
                        Log.e(
                            "mirek",
                            "Failed to get content for (probably pair or unknown) tag $tag with body $tagBody and with closing $closingTag from content:\n\n$content\n\n"
                        )
                        index += tagBody.length + 1
                        continue
                        //return
                    }

                    processPairTag(
                        tag = tag,
                        listener = listener,
                        content = tagContent,
                        startContentIndex = steIndex,
                        endContentIndex = ctei,
                        upperFlow = upperFlow,
                        ignoreOptions = ignoreOptions,
                        headData = headData
                    )

                    index = ctei + 1
                }
            }
        }
    }


    @Throws(Exception::class)
    private suspend fun processPairTag(
        tag: String,
        content: String,
        listener: HtmlArticleParserListener,
        startContentIndex: Int,
        endContentIndex: Int,
        upperFlow: FlowCollector<HtmlData>,
        ignoreOptions: IgnoreOptions,
        headData: HtmlHeadData?,
    ) {
          Log.d("mirek", "proccessing $tag with content: $content")

        var canEmitNewTag = false
        //Support pre
        when (tag) {
            "address" -> {
                canEmitNewTag = true
                listener.onAddress(
                    HtmlElementOld.Address(
                        startIndex = startContentIndex,
                        endIndex = endContentIndex,
                        content = content
                    )
                )
            }

            "table" -> {
                canEmitNewTag = true
                listener.onTable(
                    table = Parser.parseTableFromText(
                        startIndex = startContentIndex,
                        endIndex = endContentIndex,
                        content = content,
                    )
                )
            }

            "ol", "ul" -> {
                canEmitNewTag = true
                listener.onBasicList(
                    basicList = Parser.parseBasicListFromText(
                        isOrdered = tag == "ol",
                        content = content,
                        startIndex = startContentIndex,
                        endIndex = endContentIndex,
                    )
                )
            }

            "blockquote" -> {
                canEmitNewTag = true
                listener.onQuote(
                    quote = HtmlElementOld.Quote(
                        text = content,
                        startIndex = startContentIndex,
                        endIndex = startContentIndex,
                    )
                )
            }

            "code" -> {
                canEmitNewTag = true
                listener.onCode(
                    code = HtmlElementOld.Code(
                        content = content,
                        startIndex = startContentIndex,
                        endIndex = endContentIndex,
                    )
                )
            }

            "h1", "h2", "h3", "h4", "h5", "h6", "h7" -> {
                canEmitNewTag = true
                listener.onTitle(
                    title = HtmlElementOld.Title(
                        text = content,
                        startIndex = endContentIndex + 1,
                        endIndex = startContentIndex,
                        titleTag = tag
                    )
                )
            }

            "p" -> {
                canEmitNewTag = true
                //paragraph
                listener.onTextBlock(
                    textBlock = HtmlElementOld.TextBlock(
                        text = content,
                        startIndex = startContentIndex + 1,
                        endIndex = endContentIndex,
                    )
                )
            }

            else -> {
                //Other tags like div, span, section are consider to be a "wrapper" tags, trying
                //To parse content from these tags
                //fromIndex is 0 becase content is not full content, just content within tag


                Log.d("mirek", "recurse for: $content")
                parseBodyContainerTags(
                    content = content,
                    listener = listener,
                    ignoreOptions = ignoreOptions,
                    upperFlow = upperFlow,
                    fromIndex = 0,
                    headData = headData
                )
            }
        }


        if (canEmitNewTag) {
            upperFlow.emit(
                value = listener.onDataRequested(
                    headData = null,
                    HtmlData.LoadingStates(
                        isLoading = true,
                        isAppending = true,
                        message = null
                    ),
                    isFullyLoaded = false
                )
            )
        }
    }


    /**
     * Tries to parse out page title and base url from [content], all other tags are ignored since they
     * are not usefull for the library.
     * @param content Html content within <head>...</head> tag
     * @return HtmlHeadData, values can be null when title or base tags are not within [content]
     * @since 1.0.0
     */
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
            val seIndex = content.iOf(char = '>', startIndex = index)
            //TagType body within <...>
            if (index + 3 < content.length) {
                val substring = content.sub(s = index, e = index + 4)
                if (substring == "<!--") {
                    //Html comment, skipping to the next char after the comment
                    index = content.indexOfSub(substring = "-->", startIndex = index) + 1
                    continue
                }
            }

            if (index + 12 < content.length) {
                val substring = content.sub(s = index, e = index + 12)
                if (substring.equals(other = "</![cdata[//>", ignoreCase = true)) {
                    index = content.indexOfSub(
                        substring = "</![cdata[//>",
                        startIndex = index
                    ) + 1
                    continue
                }
            }

            val tagBody = content.sub(s = index + 1, e = seIndex)
            val tag = Parser.extractTagName(tagBody = tagBody)


            if (tag == "title" || tag == "base") {
                val closingTag = "</$tag>"
                val clIndex = content.indexOfSub(substring = closingTag, startIndex = index)

                val tagContent = try {
                    content.sub(s = seIndex + 1, e = clIndex)
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
                index += tagBody.length + 1
                continue
            }
        }
        return HtmlHeadData(title = title, baseUrl = base)
    }


    private suspend fun <T, R> List<T>.mapAsync(
        mapper: suspend (T) -> R
    ): List<R> = coroutineScope { map { async { mapper(it) } }.awaitAll() }
}