@file:OptIn(ExperimentalTextApi::class)

package com.jet.article.ui.elements

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.text.toSpannable
import com.jet.article.ArticleParser
import com.jet.article.data.HtmlElement
import com.jet.article.toAnnotatedString
import com.jet.article.toHtml
import com.jet.article.ui.LocalBaseArticleUrl
import com.jet.article.ui.LocalHtmlArticleData
import com.jet.article.ui.LocalLinkHandler

/**
 * @author Miroslav Hýbler <br>
 * created on 07.09.2023
 */
@Composable
fun HtmlTextBlock(
    modifier: Modifier = Modifier,
    text: HtmlElement.TextBlock,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onBackground,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {

    HtmlTextBlock(
        modifier = modifier,
        text = text.text,
        style = style,
        color = color,
        maxLines = maxLines,
        overflow = overflow,
    )
}


@Composable
fun HtmlTextBlock(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onBackground,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val linkClickHandler = LocalLinkHandler.current
    val articleUrl = LocalBaseArticleUrl.current
    val data = LocalHtmlArticleData.current
    val colorScheme = MaterialTheme.colorScheme

    val formattedText = remember(
        key1 = text,
        key2 = ArticleParser.isSimpleTextFormatAllowed,
    ) {
        if (ArticleParser.isSimpleTextFormatAllowed) {
            text.toHtml()
                .toSpannable()
                .toAnnotatedString(
                    primaryColor =colorScheme.primary,
                    linkClickHandler = linkClickHandler,
                    data = data,
                    articleUrl = articleUrl,
                )
        } else {
            buildAnnotatedString {
                append(text = text)
            }
        }
    }

    Text(
        modifier = modifier,
        text = formattedText,
        style = remember(key1 = style, key2 = color) {
            style.copy(color = color)
        },
        maxLines = maxLines,
        overflow = overflow,
    )
}