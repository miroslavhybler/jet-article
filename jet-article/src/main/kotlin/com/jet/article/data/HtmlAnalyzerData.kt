package com.jet.article.data


/**
 * @author Miroslav Hýbler <br>
 * created on 21.02.2024
 */
public sealed class HtmlAnalyzerData private constructor(
    open val range: IntRange
) {




    data object Empty : HtmlAnalyzerData(range = IntRange(start = 0, endInclusive = 0))

   data class ContentTag constructor(
        override val range: IntRange,
        val tag: TagAnalyze,
    ) : HtmlAnalyzerData(range = range) {

    }
}