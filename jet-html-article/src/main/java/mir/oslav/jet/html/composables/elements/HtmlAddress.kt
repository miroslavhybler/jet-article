package mir.oslav.jet.html.composables.elements

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.core.text.toSpannable
import mir.oslav.jet.html.LocalHtmlDimensions
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.toAnnotatedString
import mir.oslav.jet.html.toHtml


/**
 * @author Miroslav Hýbler <br>
 * created on 01.09.2023
 */
@Composable
fun HtmlAddress(
    modifier: Modifier = Modifier,
    address: HtmlElement.Address
) {

    val dimensions = LocalHtmlDimensions.current
    val colorScheme = MaterialTheme.colorScheme

    Text(
        text = remember {
            address.content.toHtml()
                .toSpannable()
                .toAnnotatedString(primaryColor =  colorScheme.primary)
        },
        modifier = modifier.padding(horizontal = dimensions.sidePadding),
        fontStyle = FontStyle.Italic
    )
}