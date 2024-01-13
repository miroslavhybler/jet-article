@file:Suppress("RedundantUnitReturnType")

package mir.oslav.jet.html.article

import mir.oslav.jet.html.article.data.HtmlContentType

/**
 * Java Native Interface (JNI) for the parser library
 * @author Miroslav Hýbler <br>
 * created on 03.01.2023
 * @since 1.0.0
 */
internal object ParserNative {


    /**
     * @since 1.0.0
     */
    external fun setInput(content: String)


    /**
     * @since 1.0.0
     */
    external fun hasNextStep(): Boolean


    /**
     * @since 1.0.0
     */
    external fun hasContent(): Boolean


    /**
     * @since 1.0.0
     */
    external fun resetCurrentContent(): Unit


    /**
     * @since 1.0.0
     */
    external fun doNextStep(): Unit


    /**
     * @since 1.0.0
     */
    @HtmlContentType
    external fun getContentType(): Int


    /**
     * @since 1.0.0
     */
    external fun getContent(): String


    /**
     * @since 1.0.0
     */
    external fun getContentListSize(): Int


    /**
     * @since 1.0.0
     */
    external fun getContentListItem(index: Int): String



    /**
     * @since 1.0.0
     */
    external fun clearAllResources(): Unit
}