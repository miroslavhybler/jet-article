package mir.oslav.jet.html.composables.elements

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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

    Text(
        text = remember {
            text.text.toHtml()
                .toSpannable()
                .toAnnotatedString(primaryColor = colorScheme.primary)
        },
        modifier = modifier.padding(horizontal = dimensions.sidePadding)
    )
}