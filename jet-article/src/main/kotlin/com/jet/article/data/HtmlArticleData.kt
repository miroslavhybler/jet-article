@file:Suppress("RedundantVisibilityModifier")

package com.jet.article.data

import androidx.annotation.Keep
import androidx.compose.runtime.Immutable


/**
 * @param url Original full url of the article
 * @param elements List of [HtmlElement] parsed out of article.
 * @param headData Metadata extracted from <head> tag.
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 15.07.2023
 */
@Keep
@Immutable
public data class HtmlArticleData public constructor(
    val url: String,
    val elements: List<HtmlElement> = emptyList(),
    val headData: HtmlHeadData = HtmlHeadData.empty,
) {
    @Keep
    companion object {
        /**
         * Empty html data, can be used to avoid nullability
         * @since 1.0.0
         */
        val empty: HtmlArticleData = HtmlArticleData(url = "")
    }

    /**
     * True when html data are empty
     * @since 1.0.0
     */
    val isEmpty: Boolean
        get() = elements.isEmpty()


}