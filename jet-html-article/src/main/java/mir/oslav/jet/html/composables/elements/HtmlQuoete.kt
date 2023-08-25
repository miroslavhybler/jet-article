package mir.oslav.jet.html.composables.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.data.HtmlElement


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

    val density = LocalDensity.current

    var dividerHeight by remember { mutableStateOf(0.dp) }

    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Divider(
            modifier = Modifier
                .size(width = 5.dp, height = dividerHeight),
            color = MaterialTheme.colorScheme.tertiary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = data.text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = HtmlDimensions.sidePadding)
                .onSizeChanged { intSize ->
                    with(density) {
                        dividerHeight = intSize.height.toDp()
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
            endIndex = 0
        )
    )

}