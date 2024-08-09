@file:Suppress("RedundantVisibilityModifier")

package com.jet.article.data


/**
 * @param elements
 * @param headData
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 15.07.2023
 */
public class HtmlArticleData public constructor(
    val url: String,
    val elements: List<HtmlElement> = emptyList(),
    val headData: HtmlHeadData = HtmlHeadData.empty,
) {

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