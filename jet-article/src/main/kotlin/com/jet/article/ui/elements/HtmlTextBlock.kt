@file:OptIn(ExperimentalTextApi::class)

package com.jet.article.ui.elements

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.text.toSpannable
import com.jet.article.data.HtmlElement
import com.jet.article.toAnnotatedString
import com.jet.article.toHtml
import com.jet.article.ui.LocalBaseArticleUrl
import com.jet.article.ui.LocalColorScheme
import com.jet.article.ui.LocalContentPadding
import com.jet.article.ui.LocalHtmlArticleData
import com.jet.article.ui.LocalLinkHandler
import com.jet.utils.dpToPx
import com.jet.utils.screenHeightPx

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
    ) {

    HtmlTextBlock(
        modifier = modifier,
        text = text.text,
        style = style,
        color = color,
    )
}


@Composable
fun HtmlTextBlock(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    val linkClickHandler = LocalLinkHandler.current
    val articleUrl = LocalBaseArticleUrl.current
    val data = LocalHtmlArticleData.current
    val configuration = LocalConfiguration.current
    val contentPadding = LocalContentPadding.current
    val density = LocalDensity.current
    val colorScheme = LocalColorScheme.current

    val screenHeightPx = configuration.screenHeightPx.toInt()
    var size by remember { mutableStateOf(value = IntSize.Zero) }

    val formattedText = remember(key1 = text) {
        text.toHtml()
            .toSpannable()
            .toAnnotatedString(primaryColor = colorScheme.linkColor)
    }

//    var initialAlpha by rememberSaveable { mutableFloatStateOf(value = 0f) }
//    val alpha = remember { Animatable(initialValue = initialAlpha) }
//
//    LaunchedEffect(key1 = text.positionKey, block = {
//        if (initialAlpha != 1f) {
//            alpha.animateTo(targetValue = 1f)
//        }
//        initialAlpha = 1f
//    })

    //TODO
    ClickableText(
        text = formattedText,
        style = remember(key1 = style, key2 = color) {
            style.copy(color = color)
        },
        onClick = { offset ->
            linkClickHandler?.handleLink(
                clickedText = formattedText,
                clickOffset = offset,
                articleUrl = articleUrl,
                data = data,
                scrollOffset = screenHeightPx
                        - size.height
                        - density.dpToPx(dp = contentPadding.calculateTopPadding())
                    .toInt()
            )
        },
        modifier = modifier
        //    .alpha(alpha = alpha.value),

    )
}