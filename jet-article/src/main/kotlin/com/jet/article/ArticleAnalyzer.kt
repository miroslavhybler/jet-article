@file:Suppress("RedundantVisibilityModifier", "MemberVisibilityCanBePrivate")

package com.jet.article

import android.util.Log
import com.jet.article.data.ContentTag
import com.jet.article.data.TagInfo
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import mir.oslav.jet.annotations.JetExperimental
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext


/**
 * Main component of the library.
 * @author Miroslav Hýbler <br>
 * created on 20.02.2024
 * @since 1.0.0
 */
@JetExperimental
public object ArticleAnalyzer {


    /**
     * @since 1.0.0
     */
    private val mAnalyzerFlow: MutableStateFlow<ContentTag?> =
        MutableStateFlow(value = null)
    public val analyzerFlow: StateFlow<ContentTag?> = mAnalyzerFlow.asStateFlow()


    private val safeCoroutineContext: CoroutineContext =
        ArticleParser.safeCoroutineContext


    suspend fun process(
        content: String,
        onTag: suspend (tag: TagInfo) -> Unit,
    ): Unit = withContext(context = safeCoroutineContext) {
        AnalyzerNative.setInput(input = content)
        jumpToBody()
        while (moveNext()) {
            val data = analyzerFlow.value
            if (data != null
                && data.tag.tag.isNotBlank()
                && !data.tag.tag.startsWith(prefix = "/")
            ) {
                onTag(data.tag)
            }
        }
    }


    private suspend fun moveNext(): Boolean = withContext(context = safeCoroutineContext) {
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
            Log.e(
                "ArticleAnalyzer",
                "Aborting with error, code $errorCode, message $errorMessage"
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
                contentType = contentType,
            )
        }
        val newData = ContentTag(
            tag = tagInfo,
            range = IntRange(
                start = contentStart,
                endInclusive = contentEnd,
            )
        )

        mAnalyzerFlow.value = newData
        return@withContext true
    }


    /**
     * @since 1.0.0
     */
    suspend fun jumpToBody() = withContext(context = safeCoroutineContext) {
        while (
            AnalyzerNative.getCurrentTag() != "body"
            && !AnalyzerNative.isAbortingWithError()
            && AnalyzerNative.hasNextStep()
        ) {
            moveNext()
        }
    }


    fun clearAllResources() {
        AnalyzerNative.clearAllResources()
    }
}