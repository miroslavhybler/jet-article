package mir.oslav.jet.html.data

import androidx.core.text.buildSpannedString


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 30.06.2023
 */
sealed class HtmlElement private constructor(
    open val span: Int
) {

    /**
     * TODO docs
     * Representing data from real html
     * @param startIndex
     * @param endIndex
     * @param span
     * @since 1.0.0
     */
    sealed class Parsed private constructor(
        open val startIndex: Int,
        open val endIndex: Int,
        override val span: Int
    ) : HtmlElement(span = span) {


        /**
         * @since 1.0.0
         */
        data class Image constructor(
            val url: String,
            val description: String?,
            override val startIndex: Int,
            override val endIndex: Int,
            override val span: Int,
        ) : Parsed(
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
        ) : Parsed(
            startIndex = startIndex,
            endIndex = endIndex,
            span = span
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
            override val span: Int
        ) : Parsed(
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
        ) : Parsed(
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
        ) : Parsed(
            startIndex = startIndex,
            endIndex = endIndex,
            span = span
        )


        /**
         * @since 1.0.0
         */
        data class Address constructor(
            val content: String,
            override val startIndex: Int,
            override val endIndex: Int,
            override val span: Int
        ) : Parsed(
            startIndex = startIndex,
            endIndex = endIndex,
            span = span
        )
    }


    /**
     * TODO docs, alternative data constructed from the parsed elements
     * @since 1.0.0
     */
    sealed class Constructed private constructor(
        override val span: Int
    ) : HtmlElement(span = span) {

        /**
         * @since 1.0.0
         */
        data class Gallery constructor(
            val images: List<Parsed.Image>,
            override val span: Int,
        ) : Constructed(span = span)


        /**
         * @since 1.0.0
         */
        data class TopBarHeader constructor(
            val title: String,
            val image: Parsed.Image?,
            override val span: Int,
        ) : Constructed(span = span)
    }
}