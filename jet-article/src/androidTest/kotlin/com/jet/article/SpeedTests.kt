package com.jet.article

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertTrue
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
        val text1 = loadAsset(fileName = "android-docs-theme")
        val text2 = loadAsset(fileName = "android-docs-material-design-guideline")

        var start1 = 0L
        var end1 = 0L
        var start2 = 0L
        var end2 = 0L


        runBlocking {
            start1 = System.currentTimeMillis()
            ArticleParser.initialize(
                areImagesEnabled = true,
                isTextFormattingEnabled = true,
                isLoggingEnabled = false,
            )
            val data1 = ArticleParser.parse(content = text1, url = "")
            end1 = System.currentTimeMillis()


            start2 = System.currentTimeMillis()
            ArticleParser.initialize(
                areImagesEnabled = true,
                isTextFormattingEnabled = true,
                isLoggingEnabled = false,
            )
            val data2 = ArticleParser.parse(content = text2, url = "")
            end2 = System.currentTimeMillis()



            val time1 = end1 - start1
            val time2 = end2 - start2
            println("SpeedTests - Time1: $time1")
            println("SpeedTests - Time2: $time2")

            //These values are valid against Pixel Fold Api 34 emulator
            //On real device, time is usually between 20-80 millis
            assertTrue(time1 < 150)
            //Next processings should be always faster than the first one
            assertTrue(time2 < 100)
            assertTrue(time2 < time1)

        }

    }


    class TestWebViewClient constructor() : WebViewClient() {

        var started: Long = 0
            private set
        var ended: Long = 0
            private set
        var resource: Long = 0
            private set
        var visible: Long = 0
            private set

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            print("SpeedTests - onPageStarted")
            started = System.currentTimeMillis()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            print("SpeedTests - onPageFinished")
            ended = System.currentTimeMillis()
        }

        override fun onLoadResource(view: WebView?, url: String?) {
            print("SpeedTests - onLoadResource")
            resource = System.currentTimeMillis()
        }

        override fun onPageCommitVisible(view: WebView?, url: String?) {
            print("SpeedTests - onPageCommitVisible")
            visible = System.currentTimeMillis()
        }
    }
}