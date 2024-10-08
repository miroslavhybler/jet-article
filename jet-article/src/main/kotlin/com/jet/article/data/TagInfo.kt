@file:Suppress("RedundantVisibilityModifier")

package com.jet.article.data

import androidx.annotation.Keep


/**
 * @author Miroslav Hýbler <br>
 * created on 13.02.2024
 */
@Keep
public sealed class TagInfo private constructor(
    open val tag: String,
    open val tagAttributes: Map<String, String> = emptyMap(),
    open val clazz: String,
    open val id: String,
    open val name: String,
    @HtmlContentType
    open val contentType: Int
) {

    abstract val isPairTag: Boolean

    @Keep
    data class Single constructor(
        override val tag: String,
        override val tagAttributes: Map<String, String> = emptyMap(),
        override val clazz: String,
        override val id: String,
        override val name: String,
        @HtmlContentType
        override val contentType: Int,
    ) : TagInfo(
        tag = tag,
        tagAttributes = tagAttributes,
        clazz = clazz,
        id = id,
        name = name,
        contentType = contentType
    ) {
        override val isPairTag: Boolean
            get() = false
    }


    @Keep
    data class Pair constructor(
        override val tag: String,
        override val tagAttributes: Map<String, String> = emptyMap(),
        override val clazz: String,
        override val id: String,
        override val name: String,
        @HtmlContentType
        override val contentType: Int,
        val content: String,
    ) : TagInfo(
        tag = tag,
        tagAttributes = tagAttributes,
        clazz = clazz,
        id = id,
        name = name,
        contentType = contentType
    ) {
        override val isPairTag: Boolean
            get() = true
    }

}