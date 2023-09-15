package mir.oslav.jet.html.composables.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.LocalHtmlDimensions
import mir.oslav.jet.html.data.Monitoring


/**
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
@Composable
fun HtmlMetrics(
    modifier: Modifier = Modifier,
    monitoring: Monitoring
) {
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .then(other = modifier)
    ) {
        ValueRow(
            title = "Duration (Millis)",
            value = monitoring.duration.toString()
        )
        ValueRow(
            title = "Average duration per tag (Millis)",
            value = String.format("%.3f", monitoring.averageDurationPerTag)
        )
        ValueRow(
            title = "Total Tags",
            value = monitoring.totalTags.toString()
        )
        ValueRow(
            title = "Ignored Tags",
            value = monitoring.ignoredTags.toString()
        )
        ValueRow(
            title = "Used Tags",
            value = monitoring.usedTags.toString()
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
        monitoring = Monitoring(
            startTime = System.currentTimeMillis() - 3230L,
            endTime = System.currentTimeMillis(),
            usedTags = 100,
            ignoredTags = 20,
            totalTags = 120,
            averageDurationPerTag = 3.0
        )
    )
}