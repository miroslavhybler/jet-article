@file:Suppress("RedundantUnitReturnType")

package com.jet.article


/**
 * @author Miroslav Hýbler <br>
 * created on 23.01.2024
 */
@Deprecated(message = "Doens't make much sence")
public object ProcessorNative {


    /**
     * Adds rule for text processing. At least one parameters should be set.
     * @param tag Set if you want to exclude specific tag, like <p> -> "p"
     * @param clazz Set if you want to exclude specific class, like "menu"
     * @param id Set if you want to exclude tag with specific id, like "menu"
     * @param keyword Set if you want to exclude tag based on its id or class by
     * specific keyword, like "cookies"
     * @since 1.0.0
     */
    external fun addRule(
        tag: String = "",
        clazz: String = "",
        id: String = "",
        keyword: String = ""
    ): Unit


    /**
     * Clears rules that were added by [addRule].
     * @since 1.0.0
     */
    external fun clearAllResources(): Unit
}