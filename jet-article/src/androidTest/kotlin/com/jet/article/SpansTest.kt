package com.jet.article

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith


/**
 * @author Miroslav Hýbler <br>
 * created on 05.10.2024
 */
@RunWith(AndroidJUnit4::class)
class SpansTest : BaseAndroidTest() {


    @Test
    fun spansTest() = runTest() {
        val text = loadAsset(fileName = "spans")
        val data = ArticleParser.parse(content = text, url = "")

    }
}