@file:Suppress("RedundantConstructorKeyword", "RedundantVisibilityModifier")

package com.jet.article.data

import androidx.annotation.Keep
import androidx.compose.ui.unit.IntSize

/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 30.06.2023
 */
@Keep
sealed class HtmlElement private constructor(
    open val id: String?
) {


    /**
     * @since 1.0.0
     */
    @Keep
    public data class Image public constructor(
        val url: String,
        val description: String?,
        val defaultSize: IntSize,
        val alt: String?,
        override val id: String?,
    ) : HtmlElement(id = id)


    /**
     * @param text Text of the text block. Can include entities formatting tags like <b>, <i>, etc when
     * [com.jet.article.ArticleParser.isSimpleTextFormatAllowed] is set to true by [com.jet.article.ArticleParser.initialize].
     * @since 1.0.0
     */
    @Keep
    public data class TextBlock public constructor(
        val text: String,
        override val id: String?,
    ) : HtmlElement(id = id)


    /**
     * @param text Styled text, title
     * @param titleTag h1, h2, h3, ...
     * @since 1.0.0
     */
    @Keep
    public data class Title public constructor(
        val text: String,
        val titleTag: String,
        override val id: String?,
    ) : HtmlElement(id = id)


    /**
     * @since 1.0.0
     */
    @Keep
    public data class Quote public constructor(
        val text: String,
        override val id: String?,
    ) : HtmlElement(id = id)


    /**
     * @since 1.0.0
     */
    @Keep
    public data class Table public constructor(
        val rows: List<List<String>>,
        override val id: String?,
    ) : HtmlElement(id = id)


    /**
     * @since 1.0.0
     */
    @Keep
    public data class BasicList public constructor(
        val items: List<String>,
        val isOrdered: Boolean,
        override val id: String?,
    ) : HtmlElement(id = id)


    /**
     * @since 1.0.0
     */
    @Keep
    public data class DescriptionList public constructor(
        val items: List<String>,
        val isOrdered: Boolean,
        override val id: String?,
    ) : HtmlElement(id = id)

    /**
     * @since 1.0.0
     */
    @Keep
    public data class Address public constructor(
        val content: String,
        override val id: String?,
    ) : HtmlElement(id = id)


    /**
     * @since 1.0.0
     */
    @Keep
    public data class Code public constructor(
        val content: String,
        override val id: String?,
    ) : HtmlElement(id = id)
}