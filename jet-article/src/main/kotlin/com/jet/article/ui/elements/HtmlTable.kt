package com.jet.article.ui.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.core.text.toSpannable
import com.jet.article.data.HtmlElement
import com.jet.article.toAnnotatedString
import com.jet.article.toHtml


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 30.06.2023
 */
@Composable
fun HtmlTable(
    modifier: Modifier = Modifier,
    data: HtmlElement.Table,
) {
    val density = LocalDensity.current

    val textMeasurer = rememberTextMeasurer()
    val typography = MaterialTheme.typography

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
            .clip(MaterialTheme.shapes.small),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSecondaryContainer),
    ) {
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.secondaryContainer)

        ) {
            data.rows.forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier
                ) {
                    row.forEachIndexed { columnIndex, value ->
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
                                        .coerceAtLeast(minimumValue = 128.dp)
                                    //     .coerceAtMost(maximumValue = dimensions.maxCellWidth)
                                }
                            } else {
                                cellWidthsForColumns[columnIndex] = widthDp
                                    .coerceAtLeast(minimumValue = 128.dp)
                                //   .coerceAtMost(maximumValue = dimensions.maxCellWidth)
                            }
                        })

                        TableCell(
                            value = value,
                            columnIndex = columnIndex,
                            rowIndex = rowIndex,
                            rowCount = data.rows.size,
                            modifier = Modifier.defaultMinSize(
                                minWidth = widthForColumn ?: 128.dp,
                                //    height = dimensions.maxCellHeight
                            )
                        )
                    }
                }

                Divider(
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

    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .sizeIn(minWidth = 128.dp, minHeight = 32.dp)
    ) {
        Text(
            text = remember(value) {
                value.toHtml()
                    .toSpannable()
                    .toAnnotatedString(primaryColor = colorScheme.primary)
            },
            modifier = Modifier
                .align(alignment = Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            style = if (rowIndex == 0)
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

        Divider(
            modifier = Modifier
                .defaultMinSize(minWidth = 1.dp)
                .fillMaxHeight(),
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
        )
    )
}