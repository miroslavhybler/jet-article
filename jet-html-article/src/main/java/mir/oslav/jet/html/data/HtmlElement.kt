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
    open val endIndex: Int,
    open val span: Int
) {


    /**
     * @since 1.0.0
     */
    data class Image constructor(
        val url: String,
        val description: String?,
        override val startIndex: Int,
        override val endIndex: Int,
        override val span: Int,
    ) : HtmlElement(
        startIndex = startIndex,
        endIndex = endIndex,
        span = span
    )


    /**
     * @since 1.0.0
     */
    data class TextBlock constructor(
        val text: String,
        override val startIndex: Int,
        override val endIndex: Int,
        override val span: Int
    ) : HtmlElement(
        startIndex = startIndex,
        endIndex = endIndex,
        span = span
    )


    /**
     * @since 1.0.0
     */
    data class Quote constructor(
        val text: String,
        override val startIndex: Int,
        override val endIndex: Int,
        override val span: Int
    ) : HtmlElement(
        startIndex = startIndex,
        endIndex = endIndex,
        span = span
    )


    /**
     * @since 1.0.0
     */
    data class Table constructor(
        val rows: List<List<String>>,
        override val startIndex: Int,
        override val endIndex: Int,
        override val span: Int
    ) : HtmlElement(
        startIndex = startIndex,
        endIndex = endIndex,
        span = span
    )


    /**
     * TODO split original html from constructed items
     * @since 1.0.0
     */
    data class Gallery constructor(
        val images: List<Image>,
        override val startIndex: Int = Int.MIN_VALUE,
        override val endIndex: Int = Int.MIN_VALUE,
        override val span: Int
    ) : HtmlElement(
        startIndex = startIndex,
        endIndex = endIndex,
        span = span
    )
}