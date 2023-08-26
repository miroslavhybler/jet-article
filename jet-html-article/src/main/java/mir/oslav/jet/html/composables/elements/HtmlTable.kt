package mir.oslav.jet.html.composables.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.data.HtmlElement


/**
 * @author Miroslav Hýbler <br>
 * created on 30.06.2023
 */
//TODO style header
//TODO separators
//TODO alignment
//TODO set styles through one object
@Composable
fun HtmlTable(
    modifier: Modifier = Modifier,
    data: HtmlElement.Table,
) {

    val density = LocalDensity.current

    Card(
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = HtmlDimensions.sidePadding)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small),
        border = BorderStroke(1.dp, Color.Black),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            data.rows.forEachIndexed { rowIndex, rowValues ->
                var rowDividerHeight by remember {
                    mutableStateOf(0.dp)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { intHeight ->
                            with(density) {
                                rowDividerHeight = intHeight.height.toDp()
                            }
                        },
                ) {
                    rowValues.forEachIndexed { columnIndex, value ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 2.dp, horizontal = 4.dp)
                        ) {
                            Text(
                                text = value,
                                modifier = Modifier.fillMaxWidth(),
                                style = if (rowIndex == 0 || columnIndex == 0)
                                    MaterialTheme.typography.titleSmall
                                else
                                    MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSecondaryContainer

                            )
                        }

                        if (columnIndex < rowValues.lastIndex) {

                            VerticalDivider(
                                modifier = Modifier
                                    .size(width = 1.dp, height = rowDividerHeight),
                                color = Color.Black
                            )
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = Color.Black
                )
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
private fun TablePreview() {
    HtmlTable(
        data = HtmlElement.Table(
            rows = listOf(
                listOf("id", "first name", "last name"),
                listOf("1", "General", "Kenobi"),
                listOf("2", "John", "Doe"),
                listOf("3232", "King Carl III.", "God Save the king!")
            ),
            startIndex = 0,
            endIndex = 0
        )
    )
}