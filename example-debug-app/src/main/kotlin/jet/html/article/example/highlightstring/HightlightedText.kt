package jet.html.article.example.highlightstring

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * @author Miroslav Hýbler <br>
 * created on 01.05.2024
 */
@Composable
fun HightlightedText(
    modifier: Modifier = Modifier,
    text: String,
    scrollState: ScrollState,
    index: Int,
) {
    val colorScheme = MaterialTheme.colorScheme
    var bounds: Rect? by remember { mutableStateOf(value = null) }

    LaunchedEffect(key1 = bounds) {
        bounds?.let {
            scrollState.scrollTo(value = it.bottom.toInt())
        }
    }


    Text(
        text = remember(key1 = index) {
            buildAnnotatedString {
                if (text.isEmpty()) {
                    append(text)
                    return@buildAnnotatedString
                }


                append(
                    text.substring(
                        startIndex = 0,
                        endIndex = (index - 10).coerceAtLeast(minimumValue = 0),
                    )
                )

                withStyle(
                    SpanStyle(
                        background = colorScheme.primaryContainer,
                        color = colorScheme.onPrimaryContainer,
                    )
                ) {
                    append(
                        text.substring(
                            startIndex = (index - 10)
                                .coerceAtLeast(minimumValue = 0),
                            endIndex = index
                                .coerceAtMost(maximumValue = text.length)
                        ),
                    )
                }
                withStyle(

                    SpanStyle(
                        background = colorScheme.primary,
                        color = colorScheme.onPrimary,
                    )
                ) {
                    append(
                        text.substring(
                            startIndex = index,
                            endIndex = index + 1
                                .coerceAtMost(maximumValue = text.length)
                        ),
                    )
                }


                withStyle(
                    SpanStyle(
                        background = colorScheme.primaryContainer,
                        color = colorScheme.onPrimaryContainer,
                    )
                ) {
                    append(
                        text.substring(
                            startIndex = index + 1,
                            endIndex = (index + 10)
                                .coerceAtMost(maximumValue = text.length)
                        ),
                    )
                }

                append(
                    text.substring(
                        startIndex = (index + 10)
                            .coerceAtMost(maximumValue = text.length),
                        endIndex = text.length,
                    )
                )
            }
        },
        onTextLayout = { textLayoutResult ->
            bounds = textLayoutResult.getBoundingBox(offset = index)
        },
        fontSize = 9.sp,
        modifier = modifier.padding(
            top = 16.dp,
            bottom = 16.dp,
            start = 4.dp,
            end = 4.dp,
        )
    )
}