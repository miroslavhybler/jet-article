package mir.oslav.jet.html.composables.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.toSpannable
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.LocalHtmlDimensions
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.html.toAnnotatedString
import mir.oslav.jet.html.toHtml


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

    val colorScheme = MaterialTheme.colorScheme
    val density = LocalDensity.current
    val dimensions = LocalHtmlDimensions.current

    var dividerHeight by remember { mutableStateOf(0.dp) }

    Row(
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        //TODO add width to dimens
        Divider(
            modifier = Modifier
                .height(height = dividerHeight)
                .width(width = 5.dp),
            color = MaterialTheme.colorScheme.tertiary,
            thickness = 5.dp,
        )

        Spacer(modifier = Modifier.width(width = dimensions.sidePadding - 5.dp))

        Text(
            text = remember {
                data.text.toHtml()
                    .toSpannable()
                    .toAnnotatedString(primaryColor = colorScheme.primary)
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = dimensions.sidePadding)
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
            text = "Hello There! This is famous quote of me, general Kenobi from the Star Wars",
            startIndex = 0,
            endIndex = 0,
            span = 1
        )
    )

}