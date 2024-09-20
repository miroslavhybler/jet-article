@file:Suppress("RedundantVisibilityModifier")

package com.jet.article.data

import androidx.annotation.Keep


/**
 * @author Miroslav Hýbler <br>
 * created on 08.12.2023
 */
@Keep
public data class HtmlHeadData internal constructor(
    val title: String?,
) {

    companion object {
        val empty: HtmlHeadData = HtmlHeadData(title = null)
    }
}