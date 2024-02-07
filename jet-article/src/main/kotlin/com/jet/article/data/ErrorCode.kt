@file:Suppress("RedundantVisibilityModifier", "RemoveEmptyPrimaryConstructor")

package com.jet.article.data

import androidx.annotation.IntDef


/**
 * @author Miroslav Hýbler <br>
 * created on 22.01.2024
 */
@IntDef(
    ErrorCode.NO_ERROR,
    ErrorCode.NO_INDEX_FOUND,
    ErrorCode.NO_CLOSING_TAG_FOUND,

    ErrorCode.CONTENT_NOT_HTML
)
public annotation class ErrorCode constructor() {

    companion object {
        const val NO_ERROR: Int = -1
        const val NO_INDEX_FOUND: Int = 1
        const val NO_CLOSING_TAG_FOUND: Int = 2
        const val CONTENT_NOT_HTML: Int = 100000


    }
}