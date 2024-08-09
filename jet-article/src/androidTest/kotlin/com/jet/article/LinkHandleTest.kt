@file:OptIn(JetExperimental::class)

package com.jet.article

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jet.article.data.HtmlArticleData
import com.jet.article.data.HtmlElement
import com.jet.article.ui.JetHtmlArticleContent
import com.jet.article.ui.Link
import com.jet.article.ui.LinkClickHandler
import com.jet.article.ui.elements.HtmlTextBlock
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import mir.oslav.jet.annotations.JetExperimental
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author Miroslav Hýbler <br>
 * created on 08.08.2024
 */
@RunWith(AndroidJUnit4::class)
class LinkHandleTest : BaseAndroidTest() {


    /**
     * [LinkClickHandler.LinkCallback] Used to query link clicks
     */
    class TestLinkClickHandler : LinkClickHandler.LinkCallback() {
        var otherDomainLinkClicks: Int = 0
            private set
        var sameDomainLinkClicks: Int = 0
            private set
        var uriLinkClicks: Int = 0
            private set
        var sectionLinkClicks: Int = 0
            private set

        override fun onOtherDomainLink(link: Link.OtherDomainLink) {
            otherDomainLinkClicks += 1
        }

        override fun onSameDomainLink(link: Link.SameDomainLink) {
            sameDomainLinkClicks += 1

        }

        override fun onUriLink(link: Link.UriLink, context: Context) {
            uriLinkClicks += 1
        }

        override fun onSectionLink(
            link: Link.SectionLink,
            lazyListState: LazyListState,
            data: HtmlArticleData,
            scrollOffset: Int
        ) {
            sectionLinkClicks += 1
        }

    }

    private val linkClickCallback: TestLinkClickHandler = TestLinkClickHandler()

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun linksTest() {
        var clicableTags: List<String> = emptyList()

        composeRule.setContent {
            var data by remember { mutableStateOf(value = HtmlArticleData.empty) }
            LaunchedEffect(key1 = Unit) {
                val text = loadAsset(fileName = "links")
                //Using example.com as default url for testing
                data = ArticleParser.parse(
                    content = text,
                    url = "https://www.example.com/article/24",
                )
                clicableTags = data.elements.filterIsInstance<HtmlElement.TextBlock>()
                    .mapNotNull { it.id }


                delay(timeMillis = 2000)

                clicableTags.forEach {
                    composeRule.onNodeWithTag(testTag = it)
                        .performClick()
                }

                //Assertion of results
                assertEquals(2, linkClickCallback.sameDomainLinkClicks)
                assertEquals(2, linkClickCallback.otherDomainLinkClicks)
                assertEquals(2, linkClickCallback.uriLinkClicks)
                assertEquals(2, linkClickCallback.sectionLinkClicks)
            }

            JetHtmlArticleContent(
                data = data,
                linkClickCallback = linkClickCallback,
                text = { text ->
                    HtmlTextBlock(
                        modifier = Modifier.testTag(
                            tag = text.id
                                ?: throw NullPointerException(
                                    "You have to set id for all text elements for this test. Tag id will be used as testTag for node compose"
                                )
                        ),
                        text = text
                    )
                }
            )
        }
    }
}