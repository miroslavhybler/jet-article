package com.jet.article.ui.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.toSpannable
import com.jet.article.data.HtmlElement
import com.jet.article.toAnnotatedString
import com.jet.article.toHtml
import com.jet.article.ui.LocalColorScheme
import com.jet.article.ui.LocalLinkHandler


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 30.06.2023
 */
@Composable
fun HtmlQuoete(
    modifier: Modifier = Modifier,
    data: HtmlElement.Quote
) {

    val colorScheme = LocalColorScheme.current
    val density = LocalDensity.current
    val linkClickHandler = LocalLinkHandler.current
    var dividerHeight by remember { mutableStateOf(0.dp) }

    Row(
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Divider(
            modifier = Modifier
                .height(height = dividerHeight)
                .width(width = 5.dp)
                .clip(shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)),
            color = colorScheme.quoteBarColor,
            thickness = 5.dp,
        )

        Spacer(modifier = Modifier.width(width = 12.dp))

        Text(
            text = remember {
                data.text.toHtml()
                    .toSpannable()
                    .toAnnotatedString(
                        primaryColor = colorScheme.quoteTextColor,
                        linkClickHandler = linkClickHandler,
                    )
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .onSizeChanged { intSize ->
                    with(density) {
                        dividerHeight = intSize.height.toDp() + 16.dp
                    }
                }
        )
    }
}


@Composable
@Preview(showBackground = true)
private fun QuotePreview() {
    HtmlQuoete(
        data = HtmlElement.Quote(
            text = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Suspendisse nisl. Quisque porta. Curabitur ligula sapien, pulvinar a vestibulum quis, facilisis vel sapien. Nullam sit amet magna in magna gravida vehicula. Morbi imperdiet, mauris ac auctor dictum, nisl ligula egestas nulla, et sollicitudin sem purus in lacus. Etiam ligula pede, sagittis quis, interdum ultricies, scelerisque eu. Mauris dolor felis, sagittis at, luctus sed, aliquam non, tellus. Ut tempus purus at lorem. Integer imperdiet lectus quis justo. Phasellus faucibus molestie nisl. Morbi scelerisque luctus velit. Phasellus et lorem id felis nonummy placerat.",
            id = ""
        )
    )

}