package com.jet.article

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Test for measuring speed and performance of the library
 * @author Miroslav Hýbler <br>
 * created on 05.08.2024
 */
@RunWith(AndroidJUnit4::class)
class SpeedTests : BaseAndroidTest() {


    @Test
    fun androidDocsTest() {
        val text = loadAsset(fileName = "android-docs-theme")

        runBlocking {
            ArticleParser.initialize(
                areImagesEnabled = true,
                isSimpleTextFormatAllowed = true,
                isLoggingEnabled = false,
            )
            val start = System.currentTimeMillis()
            val data = ArticleParser.parse(content = text, url = "")
            val end = System.currentTimeMillis()
            println("SpeedTests - Time: ${end - start}")
            assertTrue((end - start) < 100)
        }
    }
}