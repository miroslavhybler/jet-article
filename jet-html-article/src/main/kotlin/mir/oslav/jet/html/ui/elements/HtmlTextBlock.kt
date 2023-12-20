package mir.oslav.jet.html.ui.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.core.text.toSpannable
import mir.oslav.jet.html.LocalHtmlDimensions
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.toAnnotatedString
import mir.oslav.jet.html.toHtml


/**
 * @author Miroslav Hýbler <br>
 * created on 07.09.2023
 */
@Composable
fun HtmlTextBlock(
    modifier: Modifier = Modifier,
    text: HtmlElement.Parsed.TextBlock
) {

    val dimensions = LocalHtmlDimensions.current
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

    AnimatedVisibility(visible = true, enter = fadeIn()) {
        Text(
            text = remember(key1 = text) {
                text.text.toHtml()
                    .toSpannable()
                    .toAnnotatedString(primaryColor = colorScheme.primary)
            },
            modifier = modifier
                .padding(horizontal = dimensions.sidePadding)
            //    .alpha(alpha = alpha.value)

        )
    }
}