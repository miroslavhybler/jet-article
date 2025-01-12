@file:Suppress("RedundantConstructorKeyword", "RedundantVisibilityModifier")

package com.jet.article.data

import androidx.annotation.Keep
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntSize

/**
 * @param id Id of html tag element parsed from content
 * @param key Key used for [androidx.compose.foundation.lazy.LazyColumn] as unique key of item
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 30.06.2023
 */
@Keep
@Immutable
sealed class HtmlElement private constructor(
    open val id: String?,
    open val key: Int,
) {

    /**
     * @since 1.0.0
     */
    @Keep
    @Immutable
    public data class Image public constructor(
        val url: String,
        val description: String?,
        val defaultSize: IntSize,
        val alt: String?,
        override val id: String?,
        override val key: Int,
    ) : HtmlElement(id = id, key = key)


    /**
     * @param text Text of the text block. Can include entities formatting tags like <b>, <i>, etc when
     * [com.jet.article.ArticleParser.isSimpleTextFormatAllowed] is set to true by [com.jet.article.ArticleParser.initialize].
     * @since 1.0.0
     */
    @Keep
    @Immutable
    public data class TextBlock public constructor(
        val text: AnnotatedString,
        override val id: String?,
        override val key: Int,
    ) : HtmlElement(id = id, key = key)


    /**
     * @param text Styled text, title
     * @param titleTag h1, h2, h3, ...
     * @since 1.0.0
     */
    @Keep
    @Immutable
    public data class Title public constructor(
        val text: String,
        val titleTag: String,
        override val id: String?,
        override val key: Int,
    ) : HtmlElement(id = id, key = key)


    /**
     * @since 1.0.0
     */
    @Keep
    @Immutable
    public data class Quote public constructor(
        val text: String,
        override val id: String?,
        override val key: Int,
    ) : HtmlElement(id = id, key = key)


    /**
     * @since 1.0.0
     */
    @Keep
    @Immutable
    public data class Table public constructor(
        val rows: List<TableRow>,
        override val id: String?,
        override val key: Int,
    ) : HtmlElement(id = id, key = key) {

        /**
         * @param values List of values
         * @param rowKey Local unique key of table row for UI
         */
        @Keep
        @Immutable
        public data class TableRow constructor(
            val values: List<TableCell>,
            val rowKey: Int,
        ) {

            @Keep
            @Immutable
            public data class TableCell constructor(
                val value: String,
                val columnKey: Int,
            )
        }
    }


    /**
     * @since 1.0.0
     */
    @Keep
    @Immutable
    public data class BasicList public constructor(
        val items: List<String>,
        val isOrdered: Boolean,
        override val id: String?,
        override val key: Int,
    ) : HtmlElement(id = id, key = key)


    /**
     * @since 1.0.0
     */
    @Keep
    @Immutable
    public data class DescriptionList public constructor(
        val items: List<String>,
        val isOrdered: Boolean,
        override val id: String?,
        override val key: Int,
    ) : HtmlElement(id = id, key = key)

    /**
     * @since 1.0.0
     */
    @Keep
    @Immutable
    @Deprecated(message = "Probably will be replaced by TextBlock")
    public data class Address public constructor(
        val content: String,
        override val id: String?,
        override val key: Int,
    ) : HtmlElement(id = id, key = key)


    /**
     * @since 1.0.0
     */
    @Keep
    @Immutable
    public data class Code public constructor(
        val content: String,
        override val id: String?,
        override val key: Int,
    ) : HtmlElement(id = id, key = key)
}