package com.jet.article

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jet.article.data.HtmlElement
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
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
    fun singleElementsRuleTest() {
        val text = loadAsset(fileName = "exclude-1")
        ArticleParser.initialize(
            areImagesEnabled = false,
            isLoggingEnabled = true,
        )

        runBlocking {
            ContentFilterNative.addExcludeOption(keyword = "cookies")
            ContentFilterNative.addExcludeOption(id = "menu")
            ContentFilterNative.addExcludeOption(tag = "footer")
            ContentFilterNative.addExcludeOption(clazz = "advertisment")

            val data = ArticleParser.parse(content = text, url = "")

            val excluded = data.elements
                .filterIsInstance<HtmlElement.TextBlock>()
                .filter { element -> element.text == "excluded" }

            val included = data.elements
                .filterIsInstance<HtmlElement.TextBlock>()


            println(
                "ExcludedRulesTest\t---\tEXCLUDED:\n ${
                    excluded.joinToString(
                        separator = "\n",
                        transform = { "id: ${it.id}" }
                    )
                }"
            )

            assertEquals(0, excluded.size)
            assertEquals(8, included.size)

        }
    }

    /**
     * Test for file exclude-2.html
     *
     * ### Required output
     * * Not containing any text of value "excluded", these text must be excluded by the rules
     * * Size of text elements must much with occurance of ```>visible</``` in the exclude-2.html file
     */
    @Test
    fun multipleElementsRuleTest() {
        val text = loadAsset(fileName = "exclude-2")

        ContentFilterNative.addExcludeOption(
            tag = "div",
            clazz = "toRemove",
        )
        ContentFilterNative.addExcludeOption(
            tag = "div",
            id = "divToRemove"
        )
        ContentFilterNative.addExcludeOption(
            tag = "div",
            id = "divToRemove2",
            clazz = "toRemove2",
        )
        ContentFilterNative.addExcludeOption(
            id = "divToRemove3",
            clazz = "toRemove3",
        )
        runBlocking {
            val data = ArticleParser.parse(content = text, url = "")

            val excluded = data.elements
                .filterIsInstance<HtmlElement.TextBlock>()
                .filter { element -> element.text == "excluded" }

            val included = data.elements
                .filterIsInstance<HtmlElement.TextBlock>()


            println(
                "ExcludedRulesTest\t---\tEXCLUDED:\n ${
                    excluded.joinToString(
                        separator = "\n",
                        transform = { "id: ${it.id}" }
                    )
                }"
            )

            assertEquals(0, excluded.size)
            assertEquals(9, included.size)
        }
    }
}