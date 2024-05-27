@file:Suppress("RedundantConstructorKeyword", "RedundantVisibilityModifier")

package com.jet.article.data

import androidx.annotation.Keep
import androidx.compose.ui.unit.IntSize

/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 30.06.2023
 */
//TODO solve ids
@Keep
sealed class HtmlElement private constructor(
    open val id: String?
) {


    /**
     * @since 1.0.0
     */
    @Keep
    public data class Image internal constructor(
        val url: String,
        val description: String?,
        val defaultSize: IntSize,
        val alt: String?,
        override val id: String?,
    ) : HtmlElement(id = id)


    /**
     * @param text Styled text containing simple html formatting tags like b, i, u,...
     * @since 1.0.0
     */
    @Keep
    public data class TextBlock internal constructor(
        val text: String,
        override val id: String?,
    ) : HtmlElement(id = id)


    /**
     * @param text Styled text, title
     * @param titleTag h1, h2, h3, ...
     * @since 1.0.0
     */
    @Keep
    public data class Title internal constructor(
        val text: String,
        val titleTag: String,
        override val id: String?,
    ) : HtmlElement(id = id)


    /**
     * @since 1.0.0
     */
    @Keep
    public data class Quote internal constructor(
        val text: String,
        override val id: String?,
    ) : HtmlElement(id = id)


    /**
     * @since 1.0.0
     */
    @Keep
    public data class Table internal constructor(
        val rows: List<List<String>>,
        override val id: String?,
    ) : HtmlElement(id = id)


    /**
     * @since 1.0.0
     */
    @Keep
    public data class BasicList internal constructor(
        val items: List<String>,
        val isOrdered: Boolean,
        override val id: String?,
    ) : HtmlElement(id = id)


    /**
     * @since 1.0.0
     */
    @Keep
    public data class DescriptionList internal constructor(
        val items: List<String>,
        val isOrdered: Boolean,
        override val id: String?,
    ) : HtmlElement(id = id)

    /**
     * @since 1.0.0
     */
    @Keep
    public data class Address internal constructor(
        val content: String,
        override val id: String?,
    ) : HtmlElement(id = id)


    /**
     * @since 1.0.0
     */
    @Keep
    public data class Code internal constructor(
        val content: String,
        override val id: String?,
    ) : HtmlElement(id = id)
}