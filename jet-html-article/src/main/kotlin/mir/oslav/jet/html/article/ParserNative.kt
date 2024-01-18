@file:Suppress("RedundantUnitReturnType")

package mir.oslav.jet.html.article

import mir.oslav.jet.html.article.data.HtmlContentType

/**
 * Java Native Interface (JNI) for the parser library
 * @author Miroslav Hýbler <br>
 * created on 03.01.2023
 * @since 1.0.0
 */
//TODO public for making possible to create own parser ????
//TODO warmup
internal object ParserNative {


    /**
     * @since 1.0.0
     */
    external fun setInput(content: String)


    /**
     * @return
     * @since 1.0.0
     */
    external fun hasNextStep(): Boolean


    /**
     * @return
     * @since 1.0.0
     */
    external fun hasContent(): Boolean


    /**
     * @return
     * @since 1.0.0
     */
    external fun resetCurrentContent(): Unit


    /**
     * @return
     * @since 1.0.0
     */
    external fun doNextStep(): Unit


    /**
     * @return
     * @since 1.0.0
     */
    @HtmlContentType
    external fun getContentType(): Int


    /**
     * @return
     * @since 1.0.0
     */
    external fun getContent(): String


    /**
     * @return
     * @since 1.0.0
     */
    external fun getTitle(): String


    /**
     * @return
     * @since 1.0.0
     */
    external fun getBase(): String


    /**
     * @return
     * @since 1.0.0
     */
    external fun getCurrentTag(): String


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
    external fun getContentMapItem(attributeName: String): String


    /**
     * @since 1.0.0
     */
    external fun clearAllResources(): Unit


    /**
     * @since 1.0.0
     */
    external fun warmup(content: String): Unit
}