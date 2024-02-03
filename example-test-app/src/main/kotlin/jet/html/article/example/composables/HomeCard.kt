package jet.html.article.example.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jet.article.test.R


/**
 * @author Miroslav Hýbler <br>
 * created on 29.01.2024
 */
@Composable
fun HomeCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onClick: () -> Unit
) {

    Column(
        modifier = modifier
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                ),
                shape = MaterialTheme.shapes.large

            )
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.large
            )
            .clip(shape = MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Text(
            text = description,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            modifier = Modifier
                .size(size = 30.dp)
                .align(alignment = Alignment.End),

            )
    }
}


fun Modifier.spacedCard(): Modifier {
    return this
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
}


@Composable
@Preview(showBackground = true)
private fun MainCardPreview() {
    HomeCard(
        title = "Title of the card",
        description = "Description of the card, usually longer that the tilte (naturally). I should use lorem ipsum more ofther, cause i don't have idea what put into these descriptions",
        onClick = {}
    )
}