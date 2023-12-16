package mir.oslav.jet.html.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mir.oslav.jet.html.LocalHtmlDimensions
import mir.oslav.jet.html.data.HtmlParseMetering


/**
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
@Composable
fun HtmlMetrics(
    modifier: Modifier = Modifier,
    metering: HtmlParseMetering?
) {

    val dimensions = LocalHtmlDimensions.current
    if (metering == null) {
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .then(other = modifier)
    ) {
        ValueRow(
            title = "Duration (Millis)",
            value = metering.duration.toString()
        )
        ValueRow(
            title = "Tags",
            value = metering.tagsCount.toString()
        )

        Text(
            text = remember(key1 = metering) {
                metering.tags.map { (k, v) -> "$k     ::      $v" }
                    .joinToString(separator = "\n")
            },
            modifier = Modifier.padding(horizontal = dimensions.sidePadding)
        )
    }
}


@Composable
private fun ValueRow(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {

    val dimensions = LocalHtmlDimensions.current

    Row(
        modifier = Modifier
            .then(other = modifier)
            .padding(horizontal = dimensions.sidePadding, vertical = 2.dp)
            .then(other = modifier)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.weight(weight = 1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(weight = 1f),
        )
    }
}


@Composable
@Preview
private fun HtmlMetricsPreview() {

    HtmlMetrics(
        metering = HtmlParseMetering(
            startTime = System.currentTimeMillis() - 3230L,
            endTime = System.currentTimeMillis(),
            tagsCount = 0,
            tags = emptyMap()
        )
    )
}