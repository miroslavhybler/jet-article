package com.jet.article


/**
 * @author Miroslav Hýbler <br>
 * created on 23.01.2024
 */
public object ProcessorNative {


    external fun addRule(tag: String, clazz: String)


    external fun clearAllResources()
}