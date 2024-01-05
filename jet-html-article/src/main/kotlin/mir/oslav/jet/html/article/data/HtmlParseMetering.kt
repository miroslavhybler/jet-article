package mir.oslav.jet.html.article.data


/**
 * Holds the result of monitoring the parsing process
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
data class HtmlParseMetering constructor(
    val startTime: Long,
    val endTime: Long,
    val tagsCount:Int,
    val tags: Map<String, Int>
) {

    val duration: Long = endTime - startTime
}