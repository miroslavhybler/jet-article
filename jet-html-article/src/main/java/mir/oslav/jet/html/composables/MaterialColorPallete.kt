package mir.oslav.jet.html.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.LocalHtmlDimensions


/**
 * @author Miroslav Hýbler <br>
 * created on 26.08.2023
 */
@Composable
fun MaterialColorPallete(
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        ColorRow(
            title = "primary",
            colors = remember {
                listOf(
                    colorScheme.primary,
                    colorScheme.onPrimary,
                    colorScheme.primaryContainer,
                    colorScheme.onPrimaryContainer,
                    colorScheme.inversePrimary,
                )
            }
        )

        ColorRow(
            title = "secondary",
            colors = remember {
                listOf(
                    colorScheme.secondary,
                    colorScheme.onSecondary,
                    colorScheme.secondaryContainer,
                    colorScheme.onSecondaryContainer,
                    Color.Transparent
                )
            }
        )

        ColorRow(
            title = "tertiary",
            colors = remember {
                listOf(
                    colorScheme.tertiary,
                    colorScheme.onTertiary,
                    colorScheme.tertiaryContainer,
                    colorScheme.onTertiaryContainer,
                    Color.Transparent
                )
            }
        )

        ColorRow(
            title = "error",
            colors = remember {
                listOf(
                    colorScheme.error,
                    colorScheme.onError,
                    colorScheme.errorContainer,
                    colorScheme.onErrorContainer,
                    Color.Transparent
                )
            }
        )

        ColorRow(
            title = "background",
            colors = remember {
                listOf(
                    colorScheme.background,
                    colorScheme.onBackground,
                )
            }
        )
    }
}

@Composable
private fun ColorRow(
    modifier: Modifier = Modifier,
    title: String,
    colors: List<Color>
) {

    val dimensions = LocalHtmlDimensions.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.sidePadding, vertical = 2.dp)
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(weight = 1f)
        )

        colors.forEach { color ->
            Spacer(
                modifier = Modifier
                    .size(size = 26.dp)
                    .background(color = color)
            )
        }


    }
}


@Composable
@Preview
private fun PalletePreview() {

    MaterialColorPallete()
}