package mir.oslav.jet.html.parse

import android.util.Log
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.data.HtmlHeadData
import mir.oslav.jet.html.iOf
import mir.oslav.jet.html.normalizedLink
import mir.oslav.jet.html.sub
import java.util.Stack


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
internal object Parser {


    private val ignoreCaseOpt: Set<RegexOption> = setOf(RegexOption.IGNORE_CASE)

    /**
     * @since 1.0.0
     */
    //TODO try to do better
    internal fun String.indexOfSub(
        substring: String,
        startIndex: Int,
    ): Int = substring.toRegex(options = ignoreCaseOpt)
        .findAll(input = this)
        .filter { it.range.first >= startIndex }
        .map { it.range.first }
        .firstOrNull()
        ?: throw IllegalStateException(
            "Unable to get index of $substring from clipped content!\n" +
                    "Content:\n" +
                    this.sub(s = startIndex, e = this.length)
        )


    /**
     * @param rawTagWithAttributes E.g. <img src="https://www.example.com" alt="Photo"/>
     * @param headData Optional, when url from src="..." is not full url, [HtmlHeadData.baseUrl] will
     * be used as base url for the image
     * @since 1.0.0
     */
    //TODO support alt
    internal fun parseImageFromText(
        rawTagWithAttributes: String,
        startIndex: Int,
        endIndex: Int,
        headData: HtmlHeadData?,
    ): HtmlElement.Image? {
        val rawUrl = rawTagWithAttributes.split("src=")
        var url = rawUrl.lastOrNull()?.normalizedLink()

        if (url?.contains(char = ' ') == true) {
            url = url.split(' ')
                .firstOrNull()
                ?.normalizedLink()
        }

        if (url == null) {
            return null
        }

        if (
            !url.startsWith(prefix = "https://")
            || !url.startsWith(prefix = "http://")
            || !url.startsWith(prefix = "www")
        ) {
            val base = headData?.baseUrl
            if (base != null) {
                url = when {
                    base.endsWith(char = '/') && url.endsWith(char = '/') -> {
                        base.removeSuffix(suffix = "/") + url
                    }
                    base.endsWith(char = '/') || url.endsWith(char = '/') -> "$base$url"
                    else -> "$base/$url"
                }
            } else {
                //Trying to load image would result in error, null is returned instead
                return null
            }
        }

        return HtmlElement.Image(
            url = url,
            startIndex = startIndex,
            endIndex = endIndex,
            description = null
        )
    }


    /**
     * @param startIndex Start index in html parent
     * @param endIndex End index in html parent
     * @param content Doesn't do nothing with indexes
     * @return The table class
     * @since 1.0.0
     */
    internal fun parseTableFromText(
        content: String,
        startIndex: Int,
        endIndex: Int,
    ): HtmlElement.Table {
        val outRows = ArrayList<List<String>>()

        val rows = groupPairTagsContent(
            content = content,
            searchedTag = "tr",
            fromIndex = startIndex
        )

        rows.forEach { rawTag ->
            val headers = groupPairTagsContent(
                content = rawTag,
                searchedTag = "th",
                fromIndex = startIndex
            )
            val cells =
                groupPairTagsContent(
                    content = rawTag,
                    searchedTag = "td",
                    fromIndex = startIndex
                )

            outRows.add(headers + cells)
        }

        return HtmlElement.Table(
            startIndex = startIndex,
            endIndex = endIndex,
            rows = outRows,
        )
    }


    internal fun parseBasicListFromText(
        isOrdered: Boolean,
        content: String,
        startIndex: Int,
        endIndex: Int,
    ): HtmlElement.BasicList {
        val items = groupPairTagsContent(
            content = content,
            searchedTag = "li",
            fromIndex = startIndex
        )

        return HtmlElement.BasicList(
            isOrdered = isOrdered,
            startIndex = startIndex,
            endIndex = endIndex,
            items = items
        )
    }


    //TODO check if closing tag is not within comment e.g. <div> <!-- </div> -->  </div>
    internal fun findClosing(content: String, tag: String, startIndex: Int): Int {
        val stack = Stack<Pair<String, Int>>()
        var index = startIndex


        while (index in content.indices) {
            val char = content[index]
            if (char != '<') {
                index += 1
                continue
            }

            if (index + 3 < content.length) {
                val substring = content.sub(s = index, e = index + 4)
                if (substring == "<!--") {
                    //Html comment, skipping to the next char after the comment
                    index = content.indexOfSub(substring = "-->", startIndex = index) + 1
                    continue
                }
            }

            val seIndex = content.iOf(char = '>', startIndex = index)
            if (seIndex == -1) {
                Log.e(
                    "mirek",
                    "failed to create tagBody using f: ${index + 1} e: $seIndex from $content"
                )
                return -1
            }
            val tagBody = content.sub(s = index + 1, e = seIndex)
            val localTag = extractTagName(tagBody = tagBody)
            val isClosing = localTag.startsWith(char = '/')
            val isSearched = localTag
                .removePrefix(prefix = "/")
                .equals(other = tag, ignoreCase = true)

            if (isClosing && isSearched) {
                if (stack.isNotEmpty()) {
                    stack.pop()
                    index = seIndex + 1
                }

                if (stack.isEmpty()) {
                    //index = /
                    //Plus one because of closing >
                    val finalIndex = index
//                    Log.d(
//                        "mirek",
//                        "end found, checking content:${
//                            content.sub(
//                                startIndex = startIndex,
//                                endIndex = finalIndex
//                            )
//                        }"
//                    )

                    return finalIndex
                }
            }

            if (localTag.equals(other = tag, ignoreCase = true)) {
                //Same tag withing requested tag,
                stack.push(localTag to index)
            }

            index = seIndex + 1
        }

        return -1
    }


    /**
     * @return
     */
    internal fun extractTagName(tagBody: String): String {
        val rawTagName = if (tagBody.contains(char = ' ')) {
            val tagEIndex = tagBody.indexOfFirst { tagChar -> tagChar == ' ' }
            tagBody.sub(s = 0, e = tagEIndex)
        } else tagBody

        return rawTagName.trim().lowercase()
    }


    /**
     * @since 1.0.0
     */
    private fun groupPairTagsContent(
        content: String,
        searchedTag: String,
        fromIndex: Int
    ): List<String> {
        val outList = ArrayList<String>()

        var index = fromIndex

        while (index in content.indices) {
            val char = content[index]

            if (char == '<') {
                val startingTagEndIndex = content.iOf(char = '>', startIndex = index)
                val rawTag = content.sub(s = index + 1, e = startingTagEndIndex)
                var tag = rawTag

                if (tag.contains(' ')) {
                    tag = tag.split(' ').first()
                }

                if (tag == searchedTag) {
                    val closingTagStart = content.indexOfSub(
                        substring = "</$tag>",
                        startIndex = index
                    )
                    outList.add(content.sub(startingTagEndIndex + 1, closingTagStart))
                    index = closingTagStart + 1

                } else index += 1
            } else index += 1
        }
        return outList
    }
}