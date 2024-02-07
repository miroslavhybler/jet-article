package com.jet.article.ui.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.core.text.toSpannable
import com.jet.article.data.HtmlElement
import com.jet.article.toAnnotatedString
import com.jet.article.toHtml
import com.jet.article.ui.LocalBaseArticleUrl
import com.jet.article.ui.LocalHtmlData
import com.jet.article.ui.LocalLinkHandler
import mir.oslav.jet.utils.dpToPx
import mir.oslav.jet.utils.screenHeightPx


/**
 * @author Miroslav Hýbler <br>
 * created on 08.09.2023
 */
@Composable
fun HtmlTitle(
    modifier: Modifier = Modifier,
    title: HtmlElement.Title
) {

    val linkClickHandler = LocalLinkHandler.current
    val articleUrl = LocalBaseArticleUrl.current
    val data = LocalHtmlData.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val screenHeightPx = configuration.screenHeightPx.toInt()
    var size by remember { mutableStateOf(value = IntSize.Zero) }

//    var initialAlpha by rememberSaveable { mutableFloatStateOf(value = 0f) }
//    val alpha = remember { Animatable(initialValue = initialAlpha) }
//
//    LaunchedEffect(key1 = title.positionKey, block = {
//        if (initialAlpha != 1f) {
//            alpha.animateTo(targetValue = 1f)
//        }
//        initialAlpha = 1f
//    })

    val formattedText = remember(key1 = title) {
        title.text.toHtml()
            .toSpannable()
            .toAnnotatedString(primaryColor = colorScheme.primary)
    }

    val textStyle = remember(key1 = title) {
        when (title.titleTag) {
            "h1", "h2" -> typography.displaySmall
            else -> typography.titleLarge
        }
    }

    AnimatedVisibility(visible = true, enter = fadeIn()) {
        ClickableText(
            text = formattedText,
            modifier = modifier
                .fillMaxWidth()
                .onSizeChanged { newSize ->
                    size = newSize
                }, //   .alpha(alpha = alpha.value),
            style = textStyle,
            onClick = { offset ->
                linkClickHandler?.handleLink(
                    clickedText = formattedText,
                    clickOffset = offset,
                    articleUrl = articleUrl,
                    data = data,
                    scrollOffset = screenHeightPx - size.height
                )
            }
        )
    }
}


@Composable
@Preview(showBackground = true)
private fun HtmlTitlePreview() {
    HtmlTitle(
        title = HtmlElement.Title(
            text = "Jetpack Compose rules!",
            titleTag = "h1",
            id = ""
        )
    )
}