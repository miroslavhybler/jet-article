package mir.oslav.jet.html.article.ui.elements

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.text.toSpannable
import mir.oslav.jet.html.article.data.HtmlElement
import mir.oslav.jet.html.article.toAnnotatedString
import mir.oslav.jet.html.article.toHtml


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
            text.styledText.toHtml()
                .toSpannable()
                .toAnnotatedString(primaryColor = colorScheme.primary)
        },
        modifier = modifier
        //    .alpha(alpha = alpha.value)

    )

}