@file:Suppress("RedundantVisibilityModifier", "MemberVisibilityCanBePrivate")

package com.jet.article

import android.util.Log
import com.jet.article.data.HtmlAnalyzerData
import com.jet.article.data.TagAnalyze
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext


/**
 * @author Miroslav Hýbler <br>
 * created on 20.02.2024
 * @since 1.0.0
 */
public object ArticleAnalyzer {


    /**
     * @since 1.0.0
     */
    private val safeCoroutineContext: CoroutineContext = Executors
        .newSingleThreadExecutor()
        .asCoroutineDispatcher()
        .plus(context = CoroutineName(name = "JetHtmlArticleAnalyzer"))


    private val mAnalyzerFlow: MutableStateFlow<HtmlAnalyzerData> = MutableStateFlow(
        value = HtmlAnalyzerData.Empty
    )
    public val analyzerFlow: StateFlow<HtmlAnalyzerData> = mAnalyzerFlow.asStateFlow()


    suspend fun setInput(
        content: String
    ) = withContext(context = safeCoroutineContext) {
        AnalyzerNative.setInput(input = content)
    }


    suspend fun moveNext(): Unit = withContext(context = safeCoroutineContext) {
        val actualData = analyzerFlow.value

        AnalyzerNative.setRange(start = actualData.range.last, end = -1)

        if (!AnalyzerNative.hasNextStep()) {
            Log.w(
                "ArticleAnalyzer",
                "Attempt to moveNext() but NativeAnalyzer can't go next based on the range"
            )
        }

        AnalyzerNative.doNextStep()
        val tag = AnalyzerNative.getCurrentTag()
        val id = AnalyzerNative.getCurrentTagId()
        val name = AnalyzerNative.getCurrentTagName()
        val clazz = AnalyzerNative.getCurrentTagClass()
        val contentType = AnalyzerNative.getCurrentContentType()

        val attributesCount = AnalyzerNative.getCurrentTagAttributesCount()

        val attributes = if (attributesCount > 0) {
            val attrs = HashMap<String, String>()
            for (i in 0 until attributesCount) {
                val attributeName = AnalyzerNative.getCurrentAttributeName(index = i)
                val attributeValue = AnalyzerNative.getCurrentAttributeValue(name = attributeName)
                attrs[attributeName] = attributeValue
            }
            attrs
        } else emptyMap()

        //TODO check pair tag

        val tagAnalyze = TagAnalyze.Single(
            tag = tag,
            name = name,
            id = id,
            tagAttributes = attributes,
            clazz = clazz,
            contentType = contentType
        )

        val tagStart = AnalyzerNative.getCurrentTagStartIndex()
        val tagEnd = AnalyzerNative.getCurrentTagEndIndex()


        mAnalyzerFlow.value = HtmlAnalyzerData.ContentTag(
            tag = tagAnalyze,
            range = IntRange(start = tagStart, endInclusive = tagEnd)
        )
    }

    suspend fun movePrevious(): Unit = withContext(context = safeCoroutineContext) {
        TODO()
    }


    suspend fun moveInside(): Unit = withContext(context = safeCoroutineContext) {
        TODO()
    }
}