package mir.oslav.jet.html.composables.elements.topbars

import androidx.annotation.FloatRange
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.R
import kotlin.math.max
import kotlin.math.min


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 27.08.2023
 */
@Composable
fun HtmlCollapsingTopbar(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    title: String,
    shadow: Dp,
    @FloatRange(from = 0.0, to = 1.0)
    titleOffset: Float
) {

    val typography = MaterialTheme.typography
    val density = LocalDensity.current

    val textMeasurer = rememberTextMeasurer()

    var textHeight by remember { mutableStateOf(value = 0.dp) }

    LaunchedEffect(key1 = title, block = {
        textHeight = with(density) {
            textMeasurer.measure(
                text = title,
                style = typography.headlineSmall
            ).size.height.toDp()
        }
    })

    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .shadow(
                elevation = shadow,
                spotColor = MaterialTheme.colorScheme.onBackground
            )
    ) {
        Box(
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .wrapContentHeight()
                .statusBarsPadding()
                .padding(horizontal = HtmlDimensions.sidePadding, vertical = 8.dp),
        ) {

            Icon(
                painter = painterResource(id = R.drawable.ic_html_arrow_back),
                contentDescription = stringResource(id = R.string.jet_html_back_button_content_description),
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .size(size = HtmlDimensions.clickableIconSize)
                    .clip(shape = CircleShape)
                    .clickable(onClick = navHostController::popBackStack)
                    .padding(all = HtmlDimensions.clickableIconPadding),
                tint = MaterialTheme.colorScheme.onBackground
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        height = textHeight
                                + 4.dp
                                + (128.dp * titleOffset)
                    )
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = if (titleOffset == 0f) 1 else 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .offset(
                            x = HtmlDimensions.clickableIconSize * (1f - titleOffset),
                            y = (128.dp - textHeight) * titleOffset
                        )
                        /*
                        .padding(
                            top = 2.dp,
                            bottom = 2.dp,
                            start = 8.dp,
                            end = HtmlDimensions.sidePadding
                        )
                         */

                )
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
private fun SimplePreview() {

    Column {
        HtmlCollapsingTopbar(
            title = "Build better apps faster with Jetpack Compose and androidX",
            navHostController = rememberNavController(),
            titleOffset = 0f,
            shadow = Dp.Unspecified
        )

        HtmlCollapsingTopbar(
            title = "Build better apps faster with Jetpack Compose and androidX",
            navHostController = rememberNavController(),
            titleOffset = 1f,
            shadow = Dp.Unspecified
        )
    }
}