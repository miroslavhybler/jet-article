@file:Suppress("RedundantUnitReturnType")

package com.jet.article


/**
 * @author Miroslav Hýbler <br>
 * created on 23.01.2024
 */
@Deprecated(message = "Doens't make much sence")
public object ProcessorNative {


    /**
     * @since 1.0.0
     */
    external fun addRule(tag: String, clazz: String, id: String): Unit


    /**
     * @since 1.0.0
     */
    external fun clearAllResources(): Unit
}