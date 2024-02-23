@file:Suppress("RedundantVisibilityModifier")

package com.jet.article.data


/**
 * @param elements
 * @param headData
 * @param failure
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 15.07.2023
 */
public class HtmlArticleData internal constructor(
    val url: String,
    val elements: List<HtmlElement> = emptyList(),
    val headData: HtmlHeadData = HtmlHeadData.empty,
    val failure: Failure? = null
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
                && failure == null


    /**
     * @since 1.0.0
     */
    public data class Failure internal constructor(
        @ErrorCode val code: Int,
        val message: String
    )
}