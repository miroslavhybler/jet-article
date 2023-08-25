package mir.oslav.jet.html.parse

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mir.oslav.jet.html.HtmlConstants
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.data.IgnoreOptions
import mir.oslav.jet.html.data.Monitoring


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
object HtmlParser {


    /**
     * @since 1.0.0
     */
    suspend fun parse(
        content: String,
        ignoreOptions: IgnoreOptions = IgnoreOptions()
    ): HtmlData = withContext(context = Dispatchers.IO) {
        return@withContext try {
            parseRawHtmlIntoData(rawHtmlText = content)
        } catch (e: Exception) {
            e.printStackTrace()
            HtmlData.Invalid(
                title = "TODO",
                message = e.message ?: "No message"
            )
        }
    }


    private fun parseRawHtmlIntoData(
        rawHtmlText: String
    ): HtmlData.Success {
        val startTime = System.currentTimeMillis()
        val outputList = ArrayList<HtmlElement>()
        val styledMap: HashMap<Pair<Int, Int>, String> = HashMap()
        var index = 0

        var outTitle: String? = null
        var imagesCount = 0

        while (index in rawHtmlText.indices) {
            val char = rawHtmlText[index]

            if (char == '<') {
                val startingTagEndIndex = rawHtmlText.indexOf(
                    char = '>',
                    startIndex = index
                )
                val rawTag = rawHtmlText.substring(
                    startIndex = index + 1,
                    endIndex = startingTagEndIndex
                )
                var tag = rawTag

                if (tag.contains(' ')) {
                    tag = tag.split(' ').first()
                }

                if (tag == "noscript") {
                    index += rawHtmlText.indexOf(char = '>', startIndex = index)
                    continue
                }

                val isPairTag = HtmlConstants.pairTags.contains(tag)
                val isSingeTag = HtmlConstants.singleTags.contains(tag)

                if (isSingeTag) {
                    if (tag == "img") {
                        val rawUrl = rawTag.split("src=")
                        var url = rawUrl
                            .lastOrNull()
                            ?.removePrefix("\"")
                            ?.removeSuffix("/")
                            ?.removeSuffix("\"")
                            ?.removeSuffix(" ")

                        if (url?.contains(' ') == true) {
                            url = url.split(' ').firstOrNull()
                        }

                        url?.let {

                            //TODO HEADER image
                            /*
                            if (imagesCount == 0 && true) {
                                outputList.add(
                                    HtmlElement.HeaderImage(
                                        url = it,
                                        startIndex = index,
                                        endIndex = startingTagEndIndex
                                    )
                                )
                            }
                            */
                            outputList.add(
                                HtmlElement.Image(
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
                } else if (isPairTag) {
                    val closingTagStart = rawHtmlText.indexOfSubstring(
                        requestedString = "</$tag>",
                        fromIndex = index
                    ) ?: continue

                    val firstContentIndex = startingTagEndIndex + 1
                    val lastContentIndex = closingTagStart - 1
                    val tagBody = rawHtmlText.substring(firstContentIndex, closingTagStart)


                    //TODO check if tag is in head
                    if (tag == "title" && outTitle == null) {
                        outTitle = tagBody
                    }

                    val block = when {
                        tag == "table" -> parseTableFromText(
                            startIndex = startingTagEndIndex,
                            endIndex = lastContentIndex,
                            content = tagBody
                        )

                        tag == "blockquote" -> HtmlElement.Quote(
                            text = tagBody,
                            startIndex = startingTagEndIndex + 1,
                            endIndex = closingTagStart
                        )

                        else -> HtmlElement.TextBlock(
                            text = tagBody,
                            startIndex = startingTagEndIndex + 1,
                            endIndex = closingTagStart
                        )
                    }

                    if (HtmlConstants.styledTags.contains(tag)) {
                        styledMap[Pair(startingTagEndIndex + 1, closingTagStart - 1)] = tagBody
                    }

                    if (outputList.contains(block)) {
                        index = startingTagEndIndex + 1
                        continue
                    }

                    if (styledMap.values.contains(tagBody)) {
                        val pair = Pair(startingTagEndIndex + 1, closingTagStart - 1)
                        if (styledMap.keys.contains(pair)) {
                            index = startingTagEndIndex + 1
                            continue
                        }
                    }
                    outputList.add(block)
                }
                index = startingTagEndIndex + 1
            } else {
                index += 1
            }
        }

        return HtmlData.Success(
            title = outTitle ?: "TODO",
            htmlElements = outputList.also {
                /* TODO header image
                it.find { el -> el is HtmlElement.HeaderImage }?.let { hImage ->
                    it.remove(hImage)
                    it.add(0, hImage)
                }

                 */
            },
            monitoring = Monitoring.Parse(
                startTime = startTime,
                endTime = System.currentTimeMillis()
            )
        )
    }


    private fun ignoreCaseOpt(
        ignoreCase: Boolean
    ): Set<RegexOption> = if (ignoreCase)
        setOf(RegexOption.IGNORE_CASE)
    else
        emptySet()

    private fun String.indexOfSubstring(requestedString: String, fromIndex: Int): Int? =
        requestedString.toRegex(ignoreCaseOpt(true))
            .findAll(this)
            .filter { it.range.first >= fromIndex }
            .map { it.range.first }
            .firstOrNull()


    private fun parsePairTagContent(
        content: String,
        searchedTag: String
    ): String {
        var index = 0

        while (index in content.indices) {
            val char = content[index]

            if (char == '<') {
                val startingTagEndIndex = content.indexOf(
                    char = '>',
                    startIndex = index
                )
                val rawTag = content.substring(
                    startIndex = index + 1,
                    endIndex = startingTagEndIndex
                )
                var tag = rawTag

                if (tag.contains(' ')) {
                    tag = tag.split(' ').first()
                }

                if (tag == searchedTag) {
                    val closingTagStart = content.indexOfSubstring(
                        requestedString = "</$tag>",
                        fromIndex = index
                    ) ?: continue

                    return content.substring(startingTagEndIndex + 1, closingTagStart)


                } else index += 1
            }
        }
        return ""
    }

    private fun parsePairTagContents(
        content: String,
        searchedTag: String
    ): List<String> {
        val outList = ArrayList<String>()

        var index = 0

        while (index in content.indices) {
            val char = content[index]

            if (char == '<') {
                val startingTagEndIndex = content.indexOf(
                    char = '>',
                    startIndex = index
                )
                val rawTag = content.substring(
                    startIndex = index + 1,
                    endIndex = startingTagEndIndex
                )
                var tag = rawTag

                if (tag.contains(' ')) {
                    tag = tag.split(' ').first()
                }

                if (tag == searchedTag) {
                    val closingTagStart = content.indexOfSubstring(
                        requestedString = "</$tag>",
                        fromIndex = index
                    ) ?: continue

                    outList.add(content.substring(startingTagEndIndex + 1, closingTagStart))
                    index = closingTagStart + 1

                } else index += 1
            } else index += 1
        }
        return outList
    }

    /**
     * @param startIndex Start index in html parent
     * @param endIndex End index in html parent
     * @param content Doesn't do nothing with indexes
     */
    private fun parseTableFromText(
        content: String,
        startIndex: Int,
        endIndex: Int
    ): HtmlElement.Table {
        val outRows = ArrayList<List<String>>()


        val rows = parsePairTagContents(content = content, searchedTag = "tr")

        rows.forEach { rawTag ->
            val headers = parsePairTagContents(content = rawTag, searchedTag = "th")
            val cells = parsePairTagContents(content = rawTag, searchedTag = "td")

            outRows.add(headers + cells)
        }

        return HtmlElement.Table(startIndex = startIndex, endIndex = endIndex, rows = outRows)
    }
}