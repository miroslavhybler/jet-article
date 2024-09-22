@file:Suppress("ConstPropertyName")

package com.jet.article.catalog


/**
 * @author Miroslav Hýbler <br>
 * created on 20.09.2024
 */
object Routes {
    const val home: String = "home"
    const val catalog: String = "catalog/{asset}?title={title}"


    fun getRoute(item: Item): String {
        return catalog
            .replace(oldValue = "{asset}", newValue = item.asset)
            .replace(oldValue = "{title}", newValue = item.title)
    }
}