package mir.oslav.jet.html.article.ui.elements

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.text.toSpannable
import mir.oslav.jet.html.article.data.HtmlElement
import mir.oslav.jet.html.article.toAnnotatedString
import mir.oslav.jet.html.article.toHtml


/**
 * @author Miroslav Hýbler <br>
 * created on 01.09.2023
 */
@Composable
fun HtmlAddress(
    modifier: Modifier = Modifier,
    address: HtmlElement.Address
) {
    val colorScheme = MaterialTheme.colorScheme

    Text(
        text = remember {
            address.content.toHtml()
                .toSpannable()
                .toAnnotatedString(primaryColor = colorScheme.primary)
        },
        modifier = modifier,
        fontStyle = FontStyle.Italic
    )
}


@Composable
@Preview(showBackground = true)
private fun HtmlAddressPreview() {
    HtmlAddress(
        address = HtmlElement.Address(
            content = "    Written by <a href=\"mailto:webmaster@example.com\">Jon Doe</a>.<br>\n" +
                    "    Visit us at:<br>\n" +
                    "    Example.com<br>\n" +
                    "    Box 564, Disneyland<br>\n" +
                    "    USA\n"
        )
    )
}