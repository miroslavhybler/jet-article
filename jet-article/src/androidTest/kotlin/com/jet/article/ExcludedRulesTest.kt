package com.jet.article

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jet.article.data.HtmlElement
import kotlinx.coroutines.launch
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Android test for exluding rules
 * @author Miroslav Hýbler <br>
 * created on 05.08.2024
 */
@RunWith(AndroidJUnit4::class)
class ExcludedRulesTest : BaseAndroidTest() {


    /**
     * Test for file exclude-1.html
     *
     * ### Required output
     * * Not containing any text of value "excluded", these text must be excluded by the rules
     * * Size of text elements must much with occurance of ```>visible</``` in the exclude-1.html file
     */
    @Test
    fun test1() {
        val text = loadAsset(fileName = "exclude-1.html")

        coroutineScope.launch {
            ProcessorNative.addRule(keyword = "cookies")
            ProcessorNative.addRule(id = "menu")
            ProcessorNative.addRule(tag = "footer")
            ProcessorNative.addRule(clazz = "advertisment")

            val data = ArticleParser.parse(content = text, url = "")

            val exluded = data.elements
                .filterIsInstance<HtmlElement.TextBlock>()
                .filter { element -> element.text != "excluded" }

            val included = data.elements
                .filterIsInstance<HtmlElement.TextBlock>()


            assert(value = exluded.isEmpty())
            assert(value = included.size == 8)

        }
    }

}