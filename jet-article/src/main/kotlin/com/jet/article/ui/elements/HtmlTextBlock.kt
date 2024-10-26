@file:OptIn(ExperimentalTextApi::class)

package com.jet.article.ui.elements

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.util.trace
import com.jet.article.data.HtmlElement
import com.jet.article.rememberHtmlText

/**
 * @author Miroslav Hýbler <br>
 * created on 07.09.2023
 * @since 1.0.0
 */
@Composable
fun HtmlTextBlock(
    modifier: Modifier = Modifier,
    text: HtmlElement.TextBlock,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onBackground,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) = trace(sectionName = "HtmlTextBlock") {

    HtmlTextBlock(
        modifier = modifier,
        text = text.text,
        style = style,
        color = color,
        maxLines = maxLines,
        overflow = overflow,
    )
}


/**
 * @since 1.0.0
 */
@Composable
fun HtmlTextBlock(
    modifier: Modifier = Modifier,
    text: String,
    key: Int,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onBackground,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    //TODO format text before sending it to UI
    val formattedText = rememberHtmlText(key = key, text = text, linkClickHandler = null,)
    val actualStyle = remember(key1 = style, key2 = color) {
        style.copy(color = color)
    }

    HtmlTextBlock(
        modifier = modifier,
        text = formattedText,
        style = actualStyle,
        maxLines = maxLines,
        overflow = overflow,
    )
}


/**
 * @since 1.0.0
 */
@Composable
fun HtmlTextBlock(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onBackground,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val actualStyle = remember(key1 = style, key2 = color) {
        style.copy(color = color)
    }

    Text(
        modifier = modifier,
        text = text,
        style = actualStyle,
        maxLines = maxLines,
        overflow = overflow,
    )
}