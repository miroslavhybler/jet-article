package com.jet.article

import com.jet.article.data.HtmlContentType


/**
 * @author Miroslav Hýbler <br>
 * created on 19.02.2024
 */
internal object AnalyzerNative {


    /**
     * @since 1.0.0
     */
    external fun setInput(input: String): Unit


    /**
     * @since 1.0.0
     */
    external fun hasNextStep(): Boolean


    /**
     * @since 1.0.0
     */
    external fun doNextStep(): Unit


    /**
     * @since 1.0.0
     */
    external fun isAbortingWithError(): Boolean


    /**
     * @since 1.0.0
     */
    external fun getErrorCode(): Int


    /**
     * @since 1.0.0
     */
    external fun getErrorMessage(): String


    /**
     * @since 1.0.0
     */
    @HtmlContentType
    external fun getCurrentContentType(): Int

    /**
     * @return
     * @since 1.0.0
     */
    external fun getCurrentTag(): String


    /**
     * @return
     * @since 1.0.0
     */
    external fun getCurrentTagId(): String


    /**
     * @return
     * @since 1.0.0
     */
    external fun getCurrentTagName(): String


    /**
     * @since 1.0.0
     */
    external fun getCurrentTagClass(): String

    /**
     * @since 1.0.0
     */
    external fun getCurrentTagStartIndex(): Int


    /**
     * @since 1.0.0
     */
    external fun getCurrentTagEndIndex(): Int


    /**
     * @since 1.0.0
     */
    external fun hasPairTagContent(): Boolean


    /**
     * @since 1.0.0
     */
    external fun getPairTagContent(): String


    /**
     * @since 1.0.0
     */
    external fun getCurrentTagAttributesCount(): Int


    /**
     * @since 1.0.0
     */
    external fun getCurrentAttributeName(index: Int): String


    /**
     * @since 1.0.0
     */
    external fun getCurrentAttributeValue(name: String): String


    /**
     * @since 1.0.0
     */
    external fun clearAllResources(): Unit
}