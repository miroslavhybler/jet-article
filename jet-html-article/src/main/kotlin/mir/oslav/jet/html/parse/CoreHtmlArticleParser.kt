package mir.oslav.jet.html.parse

import mir.oslav.jet.html.data.HtmlConfig
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.normalizedUrl


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
internal object CoreHtmlArticleParser {


    private val ignoreCaseOpt: Set<RegexOption> = setOf(
        RegexOption.IGNORE_CASE
    )


    /**
     * @since 1.0.0
     */
    internal fun ignoreCaseOpt(
        ignoreCase: Boolean
    ): Set<RegexOption> {
        return if (ignoreCase) ignoreCaseOpt else emptySet()
    }


    /**
     * @since 1.0.0
     */
    //TODO try to do better
    internal fun String.indexOfSubstring(
        substring: String,
        fromIndex: Int
    ): Int = substring.toRegex(options = ignoreCaseOpt(ignoreCase = true))
        .findAll(input = this)
        .filter { it.range.first >= fromIndex }
        .map { it.range.first }
        .firstOrNull()
        ?: -1


    /**
     * @param rawTagWithAttributes E.g. <img src="https://www.example.com" alt="Photo"/>
     * @since 1.0.0
     */
    //TODO support alt
    internal fun parseImageFromText(
        rawTagWithAttributes: String,
        startIndex: Int,
        endIndex: Int,
        config: HtmlConfig,
    ): HtmlElement.Parsed.Image? {
        val rawUrl = rawTagWithAttributes.split("src=")
        var url = rawUrl.lastOrNull()?.normalizedUrl()

        if (url?.contains(char = ' ') == true) {
            url = url.split(' ')
                .firstOrNull()
                ?.normalizedUrl()
        }

        if (url == null) {
            return null
        }

        return HtmlElement.Parsed.Image(
            url = url,
            startIndex = startIndex,
            endIndex = endIndex,
            span = config.spanCount,
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
        config: HtmlConfig,
    ): HtmlElement.Parsed.Table {
        val outRows = ArrayList<List<String>>()

        val rows = parsePairTagBody(content = content, searchedTag = "tr")

        rows.forEach { rawTag ->
            val headers = parsePairTagBody(content = rawTag, searchedTag = "th")
            val cells = parsePairTagBody(content = rawTag, searchedTag = "td")

            outRows.add(headers + cells)
        }

        return HtmlElement.Parsed.Table(
            startIndex = startIndex,
            endIndex = endIndex,
            rows = outRows,
            span = config.spanCount
        )
    }


    /**
     * @since 1.0.0
     */
    private fun parsePairTagBody(
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
                        substring = "</$tag>",
                        fromIndex = index
                    ) ?: continue

                    outList.add(content.substring(startingTagEndIndex + 1, closingTagStart))
                    index = closingTagStart + 1

                } else index += 1
            } else index += 1
        }
        return outList
    }
}