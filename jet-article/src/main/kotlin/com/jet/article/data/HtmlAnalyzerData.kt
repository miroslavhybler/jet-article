@file:Suppress("RedundantVisibilityModifier")

package com.jet.article.data


/**
 * @author Miroslav Hýbler <br>
 * created on 21.02.2024
 */
public sealed class HtmlAnalyzerData private constructor(
    open val range: IntRange
) {

    public data object Empty : HtmlAnalyzerData(range = IntRange(start = 0, endInclusive = 0))


    public data class ContentTag internal constructor(
        override val range: IntRange,
        val tag: TagInfo,
    ) : HtmlAnalyzerData(range = range) {

    }


    public data class ParseError internal constructor(
        override val range: IntRange,
        val errorMessage: String,
        @ErrorCode val cause: Int,
    ) : HtmlAnalyzerData(range = range)
}