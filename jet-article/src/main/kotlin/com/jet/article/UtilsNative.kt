@file:Suppress("unused")

package com.jet.article


/**
 * Java Native Interface (JNI) for utils of parser library
 * @author Miroslav Hýbler <br>
 * created on 22.01.2024
 * @since 1.0.0
 */
internal object UtilsNative {


    /**
     * @since 1.0.0
     */
    external fun clearTagsFromText(input: String): String


    /**
     * @since 1.0.0
     */
    external fun clearTagsAndReplaceEntitiesFromText(input: String): String
}