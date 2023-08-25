package mir.oslav.jet.html.data


/**
 * @param startIndex
 * @param endIndex
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 30.06.2023
 */
sealed class HtmlElement private constructor(
    open val startIndex: Int,
    open val endIndex: Int
) {


    /**
     * @since 1.0.0
     */
    data class Image internal constructor(
        val url: String,
        override val startIndex: Int,
        override val endIndex: Int
    ) : HtmlElement(
        startIndex = startIndex,
        endIndex = endIndex
    )


    /**
     * @since 1.0.0
     */
    data class TextBlock internal constructor(
        val text: String,
        override val startIndex: Int,
        override val endIndex: Int
    ) : HtmlElement(
        startIndex = startIndex,
        endIndex = endIndex
    )


    /**
     * @since 1.0.0
     */
    data class Quote internal constructor(
        val text: String,
        override val startIndex: Int,
        override val endIndex: Int
    ) : HtmlElement(
        startIndex = startIndex,
        endIndex = endIndex
    )


    /**
     * @since 1.0.0
     */
    data class Table internal constructor(
        val rows: List<List<String>>,
        override val startIndex: Int,
        override val endIndex: Int
    ) : HtmlElement(
        startIndex = startIndex,
        endIndex = endIndex
    )
}