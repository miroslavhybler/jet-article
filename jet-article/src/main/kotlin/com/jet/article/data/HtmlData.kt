@file:Suppress("RedundantVisibilityModifier")

package com.jet.article.data


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 15.07.2023
 */
public sealed class HtmlData private constructor() {

    public data object Empty : HtmlData()

    public data class Success internal constructor(
        val elements: List<HtmlElement>,
        val headData: HtmlHeadData,
    ) : HtmlData()


    public data class Failure internal constructor(
        val message: String,
        val cause: Throwable?
    ) : HtmlData()
}