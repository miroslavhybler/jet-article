package com.jet.article.catalog

import android.content.Context


/**
 * @author Miroslav Hýbler <br>
 * created on 20.09.2024
 */
fun Context.getHtmlAsset(fileName: String): String {
    return String(assets.open("${fileName}.html").readBytes())
}