@file:Suppress("RedundantVisibilityModifier")

package com.jet.article.data


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 15.07.2023
 */
public class HtmlData internal constructor(
    val elements: List<HtmlElement> = emptyList(),
    val headData: HtmlHeadData = HtmlHeadData.empty,
    val failure: Failure? = null
) {

    companion object {
        val empty: HtmlData = HtmlData()
    }

    val isEmpty: Boolean
        get() = elements.isEmpty()
                && failure == null


    public data class Failure internal constructor(
        @ErrorCode val code: Int,
        val message: String
    )
}