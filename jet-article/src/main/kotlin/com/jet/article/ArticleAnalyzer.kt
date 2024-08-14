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
@Deprecated(
    message = "Analyzer is buggy and probably will be removed, this was handy during library " +
            "development but it doenst make much sence to have in library.",
    level = DeprecationLevel.WARNING,
)
public object ArticleAnalyzer {


    /**
     * @since 1.0.0
     */
    private val safeCoroutineContext: CoroutineContext = Executors
        .newSingleThreadExecutor()
        .asCoroutineDispatcher()
        .plus(context = CoroutineName(name = "JetHtmlArticleAnalyzer"))


    /**
     * @since 1.0.0
     */
    private val mAnalyzerFlow: MutableStateFlow<HtmlAnalyzerData> = MutableStateFlow(
        value = HtmlAnalyzerData.Empty
    )
    public val analyzerFlow: StateFlow<HtmlAnalyzerData> = mAnalyzerFlow.asStateFlow()


    /**
     * @since 1.0.0
     */
    private val resultData: ArrayList<HtmlAnalyzerData> = ArrayList()


    /**
     * @since 1.0.0
     */
    private var actualDataIndex: Int = -1


    /**
     * @since 1.0.0
     */
    suspend fun setInput(
        content: String
    ): Unit = withContext(context = safeCoroutineContext) {
        AnalyzerNative.setInput(input = content)
        resultData.clear()
        actualDataIndex = -1
    }


    suspend fun moveNext(): Unit = withContext(context = safeCoroutineContext) {

        if (actualDataIndex < resultData.lastIndex) {
            mAnalyzerFlow.value = resultData[++actualDataIndex]
            return@withContext
        }

        val actualData = analyzerFlow.value
        // AnalyzerNative.setRange(start = actualData.range.last, end = -1)

        if (!AnalyzerNative.hasNextStep()) {
            Log.w(
                "ArticleAnalyzer",
                "Attempt to moveNext() but NativeAnalyzer can't go next based on the range"
            )
        }

        AnalyzerNative.doNextStep()
        val contentStart = AnalyzerNative.getCurrentTagStartIndex()
        val contentEnd = AnalyzerNative.getCurrentTagEndIndex()


        if (AnalyzerNative.isAbortingWithError()) {
            val errorMessage = AnalyzerNative.getErrorMessage()
            val errorCode = AnalyzerNative.getErrorCode()

            val newData = HtmlAnalyzerData.ParseError(
                range = IntRange(start = contentStart, endInclusive = contentEnd),
                errorMessage = errorMessage,
                cause = errorCode
            )
            resultData.add(element = newData)
            //TODO index
            actualDataIndex += 1
            mAnalyzerFlow.value = newData

            return@withContext
        }

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

        val isPairTag = AnalyzerNative.hasPairTagContent()

        val tagAnalyze = if (isPairTag) {
            TagAnalyze.Pair(
                tag = tag,
                name = name,
                id = id,
                tagAttributes = attributes,
                clazz = clazz,
                contentType = contentType,
                content = AnalyzerNative.getPairTagContent()
            )

        } else {
            TagAnalyze.Single(
                tag = tag,
                name = name,
                id = id,
                tagAttributes = attributes,
                clazz = clazz,
                contentType = contentType
            )

        }
        val newData = HtmlAnalyzerData.ContentTag(
            tag = tagAnalyze,
            range = IntRange(start = contentStart, endInclusive = contentEnd)
        )

        resultData.add(element = newData)
        //TODO index
        actualDataIndex += 1
        mAnalyzerFlow.value = newData
    }

    suspend fun movePrevious(): Unit = withContext(context = safeCoroutineContext) {
        Log.w(
            "ArticleAnalyzer",
            "Attempt to movePrevious() but index is 0"
        )
        if (actualDataIndex == 0) {
            return@withContext
        }

        val data = resultData[--actualDataIndex]
        mAnalyzerFlow.value = data
    }


    suspend fun moveInside(): Unit = withContext(context = safeCoroutineContext) {
        TODO()
    }


    suspend fun jumpToBody() {
        while (
            AnalyzerNative.getCurrentTag() != "body"
            && !AnalyzerNative.isAbortingWithError()
            && AnalyzerNative.hasNextStep()
        ) {
            moveNext()
        }
    }


    fun clearAllResources() {
        resultData.clear()
        actualDataIndex = -1
        AnalyzerNative.clearAllResources()
    }
}