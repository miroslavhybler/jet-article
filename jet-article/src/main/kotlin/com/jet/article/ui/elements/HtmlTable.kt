package com.jet.article.ui.elements

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
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
import com.jet.article.rememberHtmlText
import com.jet.article.toAnnotatedString
import com.jet.article.toHtml
import com.jet.article.ui.LocalBaseArticleUrl
import com.jet.article.ui.LocalHtmlArticleData
import com.jet.article.ui.LocalLinkHandler
import com.jet.utils.dpToPx
import com.jet.utils.pxToDp
import mir.oslav.jet.annotations.JetExperimental

/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 30.06.2023
 */
//TODO needs refactor, put more login into layout layer
@Composable
@JetExperimental
fun HtmlTable(
    modifier: Modifier = Modifier,
    data: HtmlElement.Table,
    scrollState: ScrollState = rememberScrollState(),
    shape: Shape = MaterialTheme.shapes.small,
) {
    val density = LocalDensity.current

    val textMeasurer = rememberTextMeasurer()
    val typography = MaterialTheme.typography
    var width by remember(key1 = data.key) { mutableStateOf(value = 0.dp) }
    var isScrollEnabled by remember(key1 = data.key) { mutableStateOf(value = true) }
    val cellWidthsForColumns: SnapshotStateMap<Int, Dp> = remember { mutableStateMapOf() }
    val columnCount = remember(key1 = data.key) {
        data.rows.firstOrNull()?.values?.size ?: 0
    }
    val fullRowWidth by remember(key1 = data.key) {
        derivedStateOf {
            cellWidthsForColumns.values.sumOf { width ->
                with(density) { width.toPx().toDouble() }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { newSize ->
                width = with(density) { newSize.width.toDp() }
            }
            .clip(shape = shape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = shape,
            )
            .horizontalScroll(
                state = scrollState,
                enabled = isScrollEnabled,
            )
    ) {
        data.rows.forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            ) {
                row.values.forEachIndexed { columnIndex, value ->
                    var widthForColumn by remember {
                        mutableStateOf(value = cellWidthsForColumns[columnIndex])
                    }
                    var cellHeight by remember {
                        mutableStateOf(value = 0.dp)
                    }

                    LaunchedEffect(
                        key1 = value.columnKey,
                        block = {
                            val defaultWidthByColumns = width / columnCount
                            val requiredWidth = textMeasurer.measure(
                                text = value.value,
                                style = typography.titleSmall
                            ).size.width

                            val widthDp = with(density) { requiredWidth.toDp() }
                            val usableWidth = maxOf(a = defaultWidthByColumns, b = widthDp)
                            if (widthForColumn != null) {
                                if (widthDp > widthForColumn!!) {
                                    val newWidth = usableWidth
                                        .coerceAtLeast(minimumValue = 128.dp)
                                    cellWidthsForColumns[columnIndex] = newWidth
                                    Log.d("mirek", "new width: $newWidth")
                                    widthForColumn = newWidth
                                }
                            } else {
                                val newWidth = usableWidth
                                    .coerceAtLeast(minimumValue = 128.dp)
                                Log.d("mirek", "new width: $newWidth")
                                cellWidthsForColumns[columnIndex] = newWidth
                                widthForColumn = newWidth
                            }
                        }
                    )

                    TableCell(
                        modifier = Modifier
                            .width(width = widthForColumn ?: 128.dp)
                            .onSizeChanged { newSize ->
                                cellHeight = density.pxToDp(px = newSize.height)
                            },
                        value = value,
                        rowIndex = rowIndex,
                    )


                    if (columnIndex < data.rows.size) {
                        VerticalDivider(
                            modifier = Modifier.height(height = cellHeight),
                            color = MaterialTheme.colorScheme.outline,
                            thickness = 1.dp,
                        )
                    }

                }
            }

            if (rowIndex < data.rows.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier
                        .width(width = density.pxToDp(px = fullRowWidth.toFloat())),
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 1.dp
                )
            }
        }
    }
}


@Composable
private fun TableCell(
    modifier: Modifier = Modifier,
    value: HtmlElement.Table.TableRow.TableCell,
    rowIndex: Int,
) {
    val colorScheme = MaterialTheme.colorScheme
    val text = rememberHtmlText(key = value.columnKey, text = value.value)
    Box(
        modifier = modifier
            .sizeIn(minWidth = 128.dp, minHeight = 32.dp, maxHeight = 256.dp)
            .wrapContentSize()
    ) {
        Text(
            modifier = Modifier
                .align(alignment = Alignment.Center),
            text = text,
            style = if (rowIndex == 0)
                MaterialTheme.typography.titleSmall
            else
                MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = colorScheme.onBackground,
            overflow = TextOverflow.Ellipsis,
        )
    }
}


@Composable
@Preview(showBackground = true)
private fun TablePreview() {
    HtmlTable(
        data = HtmlElement.Table(
            rows = listOf(
                HtmlElement.Table.TableRow(
                    rowKey = 0,
                    values = listOf(
                        HtmlElement.Table.TableRow.TableCell(
                            columnKey = 0,
                            value = "id"
                        ),
                        HtmlElement.Table.TableRow.TableCell(
                            columnKey = 1,
                            value = "first name"
                        ),
                        HtmlElement.Table.TableRow.TableCell(
                            columnKey = 2,
                            value = "last name"
                        ),
                        HtmlElement.Table.TableRow.TableCell(
                            columnKey = 3,
                            value = "date of Birt"
                        )
                    ),
                ),
                HtmlElement.Table.TableRow(
                    rowKey = 1,
                    values = listOf(
                        HtmlElement.Table.TableRow.TableCell(
                            columnKey = 0,
                            value = "1"
                        ),
                        HtmlElement.Table.TableRow.TableCell(
                            columnKey = 1,
                            value = "John"
                        ),
                        HtmlElement.Table.TableRow.TableCell(
                            columnKey = 2,
                            value = "Doe"
                        ),
                        HtmlElement.Table.TableRow.TableCell(
                            columnKey = 3,
                            value = "1990-01-01"
                        )
                    ),
                ),
            ),
            key = 0,
            id = null,
        )
    )
}