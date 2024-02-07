@file:OptIn(ExperimentalTextApi::class)
@file:Suppress("RedundantVisibilityModifier")

package com.jet.article.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.core.text.toSpannable
import com.jet.article.data.HtmlElement
import com.jet.article.toAnnotatedString
import com.jet.article.toHtml
import com.jet.article.ui.LocalBaseArticleUrl
import com.jet.article.ui.LocalHtmlData
import com.jet.article.ui.LocalLinkHandler


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 13.01.2024
 */
//TODO drawable
@Composable
public fun HtmlBasicList(
    modifier: Modifier = Modifier,
    list: HtmlElement.BasicList
) {
    val linkClickHandler = LocalLinkHandler.current
    val articleUrl = LocalBaseArticleUrl.current
    val data = LocalHtmlData.current
    val colorScheme = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth()) {
        list.items.forEachIndexed { index, s ->
            val formattedText = remember(key1 = list) {
                val text = if (list.isOrdered) "${index + 1}. $s" else s
                text.toHtml()
                    .toSpannable()
                    .toAnnotatedString(primaryColor = colorScheme.primary)
            }
            ClickableText(
                text = formattedText,
                style = MaterialTheme.typography.bodyMedium.copy(color = colorScheme.onBackground),
                onClick = { offset ->
                    linkClickHandler?.handleLink(
                        clickedText = formattedText,
                        clickOffset = offset,
                        articleUrl = articleUrl,
                        data = data,
                        scrollOffset = 0 //TODO
                    )
                }
            )
        }
    }
}