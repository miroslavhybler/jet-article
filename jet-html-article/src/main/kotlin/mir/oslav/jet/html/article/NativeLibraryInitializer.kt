@file:Suppress("unused", "RedundantVisibilityModifier")

package mir.oslav.jet.html.article

import android.content.Context
import androidx.startup.Initializer


/**
 * Initializer used to load native library at the target application startup
 * @author Miroslav Hýbler <br>
 * created on 08.01.2024
 */
public class NativeLibraryInitializer public constructor() : Initializer<Unit> {


    override fun create(context: Context) {
        System.loadLibrary("article")
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}