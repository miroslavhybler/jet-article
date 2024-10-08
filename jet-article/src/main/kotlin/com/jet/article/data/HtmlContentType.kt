@file:Suppress("RedundantVisibilityModifier", "RemoveEmptyPrimaryConstructor")

package com.jet.article.data

import androidx.annotation.IntDef


/**
 * Simplifes meaning of html tag.
 * Must be exactly same as cpp/utils/Constants.h with enum TagType.
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 10.01.2024
 */
@IntDef(
    HtmlContentType.NO_CONTENT,
    HtmlContentType.IMAGE,
    HtmlContentType.TEXT,
    HtmlContentType.QUOTE,
    HtmlContentType.TITLE,
    HtmlContentType.TABLE,
    HtmlContentType.ADDRESS,
    HtmlContentType.LIST,
    HtmlContentType.CODE,
)
public annotation class HtmlContentType constructor() {

    companion object {
        internal const val NO_CONTENT: Int = -1
        internal const val IMAGE: Int = 1
        internal const val TEXT: Int = 2
        internal const val QUOTE: Int = 3
        internal const val TITLE: Int = 4
        internal const val TABLE: Int = 5
        internal const val ADDRESS: Int = 6
        internal const val LIST: Int = 7
        internal const val CODE: Int = 8


        /**
         * @since 1.0.0
         */
        fun toString(@HtmlContentType value: Int): String {
            return when (value) {
                NO_CONTENT -> "No Content"
                IMAGE -> "Image"
                TEXT -> "Text"
                QUOTE -> "Quoete"
                TITLE -> "Title"
                TABLE -> "Table"
                ADDRESS -> "Address"
                LIST -> "List"
                CODE -> "Code"
                else -> "Unknown"
            }
        }
    }
}