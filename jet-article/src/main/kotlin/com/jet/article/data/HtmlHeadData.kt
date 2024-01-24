@file:Suppress("RedundantVisibilityModifier")

package com.jet.article.data


/**
 * @author Miroslav Hýbler <br>
 * created on 08.12.2023
 */
public data class HtmlHeadData internal constructor(
    val title: String?,
    val baseUrl: String?,
) {

    companion object {
        val empty: HtmlHeadData = HtmlHeadData(title = null, baseUrl = null)
    }
}