package mir.oslav.jet.html.composables.elements

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.data.HtmlElement


private val minCellWidth: Dp = 96.dp
private val minCellHeight: Dp = 32.dp

private val maxCellWidth: Dp = 192.dp
private val maxCellHeight: Dp = 32.dp

/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 30.06.2023
 */
//TODO set styles through one object
@Composable
fun HtmlTable(
    modifier: Modifier = Modifier,
    data: HtmlElement.Table,
) {
    val typography = MaterialTheme.typography
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val cellWidthsForColumns: SnapshotStateMap<Int, Dp> = remember { mutableStateMapOf() }

    val fullRowWidth by remember {
        derivedStateOf {
            cellWidthsForColumns.values.sumOf { width ->
                with(density) { width.toPx().toDouble() }
            }
        }
    }

    val rowsCount = remember { data.rows.size }
    val columnCount = remember { data.rows.firstOrNull()?.size ?: 0 }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(state = rememberScrollState())
            .padding(horizontal = HtmlDimensions.sidePadding)
            .clip(MaterialTheme.shapes.small),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSecondaryContainer),
    ) {
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.secondaryContainer)

        ) {
            data.rows.forEachIndexed { rowIndex, rowValues ->
                Row(
                    modifier = Modifier
                ) {
                    rowValues.forEachIndexed { columnIndex, value ->
                        val widthForColumn = cellWidthsForColumns[columnIndex]

                        LaunchedEffect(key1 = Unit, block = {
                            val requiredWidth = textMeasurer.measure(
                                text = value,
                                style = typography.titleSmall
                            ).size.width
                            val widthDp = with(density) { requiredWidth.toDp() }

                            if (widthForColumn != null) {
                                if (widthDp > widthForColumn) {
                                    cellWidthsForColumns[columnIndex] = widthDp
                                        .coerceAtLeast(minimumValue = minCellWidth)
                                        .coerceAtMost(maximumValue = maxCellWidth)
                                }
                            } else {
                                cellWidthsForColumns[columnIndex] = widthDp
                                    .coerceAtLeast(minimumValue = minCellWidth)
                                    .coerceAtMost(maximumValue = maxCellWidth)
                            }
                        })

                        TableCell(
                            value = value,
                            columnIndex = columnIndex,
                            rowIndex = rowIndex,
                            rowCount = rowValues.size,
                            modifier = Modifier.size(
                                width = widthForColumn ?: minCellWidth,
                                height = maxCellHeight
                            )
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.width(width = with(density) {
                        fullRowWidth.toFloat().toDp() + 1.dp * columnCount
                    }),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    thickness = 1.dp
                )
            }
        }
    }
}


@Composable
private fun TableCell(
    modifier: Modifier = Modifier,
    value: String,
    columnIndex: Int,
    rowIndex: Int,
    rowCount: Int,
) {
    Box(
        modifier = modifier
            .sizeIn(
                minWidth = minCellWidth,
                maxWidth = maxCellWidth,
                minHeight = minCellHeight,
                maxHeight = maxCellHeight
            )
    ) {
        Text(
            text = value,
            modifier = Modifier
                .align(alignment = Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            style = if (rowIndex == 0 || columnIndex == 0)
                MaterialTheme.typography.titleSmall
            else
                MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis

        )
    }

    if (columnIndex < (rowCount - 1)) {

        VerticalDivider(
            modifier = Modifier
                .size(width = 1.dp, height = maxCellHeight),
            color = Color.Black,
            thickness = 1.dp
        )
    }
}


@Composable
@Preview(showBackground = true)
private fun TablePreview() {
    HtmlTable(
        data = HtmlElement.Table(
            rows = listOf(
                listOf("id", "first name", "last name", "date of Birt"),
                listOf("1", "General", "Kenobi", "1. 1. 2022"),
                listOf("2", "John", "Doe", "24. 5. 1989"),
                listOf("3232", "King Carl III.", "God Save the king!", "1. 6. 1960")
            ),
            startIndex = 0,
            endIndex = 0,
            span = 1
        )
    )
}