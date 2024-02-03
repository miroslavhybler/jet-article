package com.jet.article.ui.elements

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.text.toSpannable
import com.jet.article.data.HtmlElement
import com.jet.article.toAnnotatedString
import com.jet.article.toHtml


/**
 * @author Miroslav Hýbler <br>
 * created on 07.09.2023
 */
@Composable
fun HtmlTextBlock(
    modifier: Modifier = Modifier,
    text: HtmlElement.TextBlock
) {
    val colorScheme = MaterialTheme.colorScheme

//    var initialAlpha by rememberSaveable { mutableFloatStateOf(value = 0f) }
//    val alpha = remember { Animatable(initialValue = initialAlpha) }
//
//    LaunchedEffect(key1 = text.positionKey, block = {
//        if (initialAlpha != 1f) {
//            alpha.animateTo(targetValue = 1f)
//        }
//        initialAlpha = 1f
//    })

    Text(
        text = remember(key1 = text) {
            text.text.toHtml()
                .toSpannable()
                .toAnnotatedString(primaryColor = colorScheme.primary)
        },
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
        //    .alpha(alpha = alpha.value)

    )

}