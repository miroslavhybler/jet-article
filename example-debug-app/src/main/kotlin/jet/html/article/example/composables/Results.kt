package jet.html.article.example.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jet.html.article.example.data.TestResults


/**
 * @author Miroslav Hýbler <br>
 * created on 05.02.2024
 */
@Composable
fun Results(modifier: Modifier = Modifier, results: TestResults) {

    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {

        ValueRow(
            label = "Millis (avg)",
            value = remember(key1 = results) { String.format("%.0f", results.millisAverage) },
        )

        ValueRow(
            label = "Nano (avg)",
            value = remember(key1 = results) { String.format("%.0f", results.nanoAverage) },
        )
    }
}


@Composable
private fun ValueRow(modifier: Modifier = Modifier, label: String, value: String) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(weight = 1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(weight = 1f)
        )
    }
}