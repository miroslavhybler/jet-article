@file:Suppress("unused", "RedundantVisibilityModifier")

package com.jet.article

import android.content.Context
import androidx.startup.Initializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.jet.article.ui.JetHtmlArticle


/**
 * Initializer used to load native library at the target application startup
 * @author Miroslav Hýbler <br>
 * created on 08.01.2024
 */
public class NativeLibraryInitializer public constructor() : Initializer<Unit> {


    override fun create(context: Context) {
        System.loadLibrary("jet-article")
        //   CoroutineScope(Dispatchers.Default).launch {
        //    JetHtmlArticleParser.warmup(context = context)
        //       }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}