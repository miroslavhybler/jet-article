package com.jet.article

import android.content.Context
import android.content.res.AssetManager
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


/**
 * @author Miroslav Hýbler <br>
 * created on 05.08.2024
 */
abstract class BaseAndroidTest public constructor() {

    protected val context: Context
        get() = InstrumentationRegistry.getInstrumentation().context

    protected val assets: AssetManager
        get() = context.assets

    /**
     * @param fileName File name in assets folder without extension
     */
    protected fun loadAsset(fileName: String): String {
        return assets.open("$fileName.html")
            .bufferedReader()
            .readText()
    }
}