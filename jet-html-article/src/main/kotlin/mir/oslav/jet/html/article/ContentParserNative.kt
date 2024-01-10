@file:Suppress("RedundantUnitReturnType")

package mir.oslav.jet.html.article

import mir.oslav.jet.html.article.data.HtmlContentType

/**
 * Java Native Interface (JNI) for the parser library
 * @author Miroslav Hýbler <br>
 * created on 03.01.2023
 */
internal object ContentParserNative {

    external fun setContent(content: String)


    external fun hasNextStep(): Boolean


    //TODO remove, bude check na ContentType
    external fun hasContent(): Boolean


    external fun doNextStep(): Unit


    @HtmlContentType
    external fun getContentType(): Int


    external fun getContent(): String


    external fun clearAllResources(): Unit
}