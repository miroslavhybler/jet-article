package mir.oslav.jet.html


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 30.06.2023
 */
@Deprecated(message = "Use new")
sealed class HtmlElementOld private constructor(
    open val startIndex: Int,
    open val endIndex: Int,
) {

    val positionKey = PositionKey(startIndex = startIndex, endIndex = endIndex)

    data class PositionKey constructor(val startIndex: Int, val endIndex: Int)


    /**
     * @since 1.0.0
     */
    data class Image constructor(
        val url: String,
        val description: String?,
        override val startIndex: Int,
        override val endIndex: Int,
    ) : HtmlElementOld(
        startIndex = startIndex,
        endIndex = endIndex,
    )


    /**
     * @since 1.0.0
     */
    data class TextBlock constructor(
        val text: String,
        override val startIndex: Int,
        override val endIndex: Int,
    ) : HtmlElementOld(
        startIndex = startIndex,
        endIndex = endIndex,
    )


    /**
     * @param text Content text of title
     * @param titleTag h1, h2, h3, ...
     * @since 1.0.0
     */
    data class Title constructor(
        val text: String,
        val titleTag: String,
        override val startIndex: Int,
        override val endIndex: Int,
    ) : HtmlElementOld(
        startIndex = startIndex,
        endIndex = endIndex,
    )


    /**
     * @since 1.0.0
     */
    data class Quote constructor(
        val text: String,
        override val startIndex: Int,
        override val endIndex: Int,
    ) : HtmlElementOld(
        startIndex = startIndex,
        endIndex = endIndex,
    )


    /**
     * @since 1.0.0
     */
    data class Table constructor(
        val rows: List<List<String>>,
        override val startIndex: Int,
        override val endIndex: Int,
    ) : HtmlElementOld(
        startIndex = startIndex,
        endIndex = endIndex,
    )


    /**
     * @since 1.0.0
     */
    data class BasicList(
        val items: List<String>,
        val isOrdered: Boolean,
        override val startIndex: Int,
        override val endIndex: Int,
    ) : HtmlElementOld(
        startIndex = startIndex,
        endIndex = endIndex,
    )


    /**
     * @since 1.0.0
     */
    data class DescriptionList(
        val items: List<String>,
        val isOrdered: Boolean,
        override val startIndex: Int,
        override val endIndex: Int,
        val span: Int
    ) : HtmlElementOld(
        startIndex = startIndex,
        endIndex = endIndex,
    )

    /**
     * @since 1.0.0
     */
    data class Address constructor(
        val content: String,
        override val startIndex: Int,
        override val endIndex: Int,
    ) : HtmlElementOld(
        startIndex = startIndex,
        endIndex = endIndex,
    )

    data class Code constructor(
        val content: String,
        override val startIndex: Int,
        override val endIndex: Int,
    ) : HtmlElementOld(
        startIndex = startIndex,
        endIndex = endIndex,
    )
}