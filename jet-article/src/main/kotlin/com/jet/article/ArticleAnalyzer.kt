@file:Suppress("RedundantVisibilityModifier", "MemberVisibilityCanBePrivate")

package com.jet.article

import android.util.Log
import com.jet.article.data.HtmlAnalyzerData
import com.jet.article.data.TagInfo
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
    message = "Analyzer is buggy and probably will be removed or replaced by different component." +
            "This was handy during library development but it doenst make much sence to have it.",
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
    private val mResultData: ArrayList<HtmlAnalyzerData> = ArrayList()
    val resultData: List<HtmlAnalyzerData> get() = mResultData

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
        mResultData.clear()
        actualDataIndex = -1
    }


    suspend fun moveNext(): Boolean = withContext(
        context = safeCoroutineContext
    ) {

        if (actualDataIndex < mResultData.lastIndex) {
            mAnalyzerFlow.value = mResultData[++actualDataIndex]
            return@withContext true
        }

        val actualData = analyzerFlow.value
        // AnalyzerNative.setRange(start = actualData.range.last, end = -1)

        if (!AnalyzerNative.hasNextStep()) {
            Log.w(
                "ArticleAnalyzer",
                "Attempt to moveNext() but NativeAnalyzer can't go next, probably it's on the end of document"
            )

            return@withContext false
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
            mResultData.add(element = newData)
            //TODO index
            actualDataIndex += 1
            mAnalyzerFlow.value = newData

            Log.e(
                "ArticleAnalyzer",
                "Aborting with error $newData"
            )

            return@withContext false
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

        val tagInfo = if (isPairTag) {
            TagInfo.Pair(
                tag = tag,
                name = name,
                id = id,
                tagAttributes = attributes,
                clazz = clazz,
                contentType = contentType,
                content = AnalyzerNative.getPairTagContent()
            )

        } else {
            TagInfo.Single(
                tag = tag,
                name = name,
                id = id,
                tagAttributes = attributes,
                clazz = clazz,
                contentType = contentType
            )

        }
//        Log.d(
//            "ArticleAnalyzer", "newInfo: $tagInfo"
//        )
        val newData = HtmlAnalyzerData.ContentTag(
            tag = tagInfo,
            range = IntRange(start = contentStart, endInclusive = contentEnd)
        )

        mResultData.add(element = newData)
        //TODO index
        actualDataIndex += 1
        mAnalyzerFlow.value = newData
        return@withContext true
    }

    suspend fun movePrevious(): Unit = withContext(context = safeCoroutineContext) {
        Log.w(
            "ArticleAnalyzer",
            "Attempt to movePrevious() but index is 0"
        )
        if (actualDataIndex == 0) {
            return@withContext
        }

        val data = mResultData[--actualDataIndex]
        mAnalyzerFlow.value = data
    }


    //TODO move out somehow
    //TODO parser component nepočítá s range
    suspend fun moveInside(): Boolean = withContext(context = safeCoroutineContext) {
        val data = analyzerFlow.value
        if (data is HtmlAnalyzerData.ContentTag && data.tag.isPairTag) {
            (data.tag as TagInfo.Pair).let {
                AnalyzerNative.setRange(
                    start = data.range.first,
                    end = data.range.last,
                )
                return@withContext true
            }
        }


        return@withContext false
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
        mResultData.clear()
        actualDataIndex = -1
        AnalyzerNative.clearAllResources()
    }
}