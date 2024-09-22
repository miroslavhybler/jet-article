package com.jet.article.ui.elements

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jet.article.data.HtmlElement


/**
 * @author Miroslav Hýbler <br>
 * created on 01.09.2023
 */
@Composable
fun HtmlAddress(
    modifier: Modifier = Modifier,
    address: HtmlElement.Address
) {
    HtmlTextBlock(
        text = address.content,
        key = address.key,
        modifier = modifier
    )
}


@Composable
@Preview(showBackground = true)
private fun HtmlAddressPreview() {
    HtmlAddress(
        address = HtmlElement.Address(
            content = "  Written by <a href=\"mailto:webmaster@example.com\">Jon Doe</a>.<br>\n" +
                    "    Visit us at:<br>\n" +
                    "    Example.com<br>\n" +
                    "    Box 564, Disneyland<br>\n" +
                    "    USA\n",
            id = "",
            key = 0,
        )
    )
}