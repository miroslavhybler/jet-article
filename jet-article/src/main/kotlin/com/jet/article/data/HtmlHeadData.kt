@file:Suppress(
    "RedundantVisibilityModifier",
    "DATA_CLASS_COPY_VISIBILITY_WILL_BE_CHANGED_WARNING"
)

package com.jet.article.data

import androidx.annotation.Keep
import androidx.compose.runtime.Immutable


/**
 * @author Miroslav Hýbler <br>
 * created on 08.12.2023
 */
@Keep
@Immutable
public data class HtmlHeadData internal constructor(
    val title: String?,
) {

    @Keep
    companion object {
        val empty: HtmlHeadData
            get() = HtmlHeadData(title = null)
    }
}