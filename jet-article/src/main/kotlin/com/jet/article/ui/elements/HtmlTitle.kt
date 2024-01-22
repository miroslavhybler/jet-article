package com.jet.article.ui.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.text.toSpannable
import com.jet.article.data.HtmlElement
import com.jet.article.toAnnotatedString
import com.jet.article.toHtml


/**
 * @author Miroslav Hýbler <br>
 * created on 08.09.2023
 */
@Composable
fun HtmlTitle(
    modifier: Modifier = Modifier,
    title: HtmlElement.Title
) {

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

//    var initialAlpha by rememberSaveable { mutableFloatStateOf(value = 0f) }
//    val alpha = remember { Animatable(initialValue = initialAlpha) }
//
//    LaunchedEffect(key1 = title.positionKey, block = {
//        if (initialAlpha != 1f) {
//            alpha.animateTo(targetValue = 1f)
//        }
//        initialAlpha = 1f
//    })

    //TODO by configuration too
    val textStyle = remember(key1 = title) {
        when (title.titleTag) {
            "h1" -> typography.displayLarge
            "h2" -> typography.displayMedium
            "h3" -> typography.displaySmall
            "h4" -> typography.headlineLarge
            "h5" -> typography.headlineMedium
            "h6" -> typography.headlineSmall
            "h7" -> typography.titleLarge
            else -> throw IllegalStateException("TagType ${title.titleTag} is not supported!")
        }
    }

    AnimatedVisibility(visible = true, enter = fadeIn()) {
        Text(
            text = remember(key1 = title) {
                title.text.toHtml()
                    .toSpannable()
                    .toAnnotatedString(primaryColor = colorScheme.primary)
            },
            modifier = modifier
                .fillMaxWidth()
            , //   .alpha(alpha = alpha.value),
            style = textStyle
        )
    }
}


@Composable
@Preview
private fun HtmlTitlePreview() {
    HtmlTitle(title = HtmlElement.Title(text = "Jetpack Compose rules!", titleTag = "h1",))
}