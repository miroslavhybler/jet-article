package mir.oslav.jet.html.composables.elements.topbars

import android.util.Log
import androidx.annotation.FloatRange
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.LocalHtmlDimensions
import mir.oslav.jet.html.R
import kotlin.math.roundToInt


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
    scrollOffset: Offset,
    state: CollapsingTopBarState,
) {
    val typography = MaterialTheme.typography
    val dimensions = LocalHtmlDimensions.current
    val density = LocalDensity.current

    val minHeight = dimensions.collapsingTopBar.minHeight

    val textMeasurer = rememberTextMeasurer()
    val textHeight = remember {
        with(density) {
            textMeasurer.measure(
                text = title,
                style = typography.headlineSmall
            ).size.height.toDp()
        }
    }

    val topbarDimens = dimensions.collapsingTopBar


    val maxTitleOffset = dimensions.collapsingTopBar.maxHeight + (textHeight * 4)
    val maxTitleOffsetPx = with(density) { maxTitleOffset.toPx() }


    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(
                min = dimensions.collapsingTopBar.minHeight,
                max = dimensions.collapsingTopBar.maxHeight
            )
            .statusBarsPadding()
            .background(color = Color.Blue.copy(alpha = 0.4f))
            .padding(horizontal = dimensions.sidePadding)
            .shadow(
                elevation = shadow,
                spotColor = MaterialTheme.colorScheme.onBackground
            )
            .clipToBounds()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = minHeight)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_html_arrow_back),
                contentDescription = stringResource(id = R.string.jet_html_back_button_content_description),
                modifier = Modifier
                    .align(alignment = Alignment.CenterStart)
                    .size(size = dimensions.clickableIconSize)
                    .clip(shape = CircleShape)
                    .clickable(onClick = navHostController::popBackStack)
                    .padding(all = dimensions.clickableIconPadding),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }


        Log.d("mirek", "Box: ${state.height}")

        Box(
            modifier = Modifier
                .align(alignment = Alignment.Center)
                .fillMaxWidth()
                .height(height = state.height)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(
                        start = 8.dp,
                        end = (dimensions.sidePadding)
                            .coerceAtLeast(minimumValue = 0.dp)
                    )
                    .offset(y = state.height - textHeight * 2)
                    .fillMaxWidth()

            )
        }
    }
}


class CollapsingTopBarState internal constructor(
    topBarDimens: HtmlDimensions.CollapsingTopBar,
    val density: Density,
) {

    private val maxHeight = topBarDimens.maxHeight
    private val minHeight = topBarDimens.minHeight
    private val maxHeightPx = with(density) { maxHeight.toPx() }
    private val minHeightPx = with(density) { minHeight.toPx() }

    var height: Dp by mutableStateOf(value = maxHeight)
    val heightPx: Float get() = with(density) { height.toPx() }


    val progress: Float
        @FloatRange(
            from = 0.0,
            to = 1.0
        ) get() = (heightPx - minHeightPx) / (maxHeightPx - minHeightPx)


    fun collapse(scrollOffset: Offset, available: Offset): Offset {
        height = with(density) {
            (maxHeightPx + scrollOffset.y)
                .coerceAtMost(maximumValue = maxHeightPx)
                .coerceAtLeast(minimumValue = minHeightPx)
                .toDp()
        }
        val consumed = if (heightPx < minHeightPx) available else Offset.Zero

        return consumed
    }
}


@Composable
fun rememberCollapsingTopBarState(): CollapsingTopBarState {
    val density = LocalDensity.current
    val dimens = LocalHtmlDimensions.current
    val topBarDimens = dimens.collapsingTopBar

    return remember {
        CollapsingTopBarState(
            density = density,
            topBarDimens = topBarDimens
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun SimplePreview() {

    Column {
        HtmlCollapsingTopbar(
            title = "Build better apps faster with Jetpack Compose and androidX",
            navHostController = rememberNavController(),
            shadow = Dp.Unspecified,
            scrollOffset = Offset.Zero,
            state = rememberCollapsingTopBarState()
        )

        HtmlCollapsingTopbar(
            title = "Build better apps faster with Jetpack Compose and androidX",
            navHostController = rememberNavController(),
            shadow = Dp.Unspecified,
            scrollOffset = Offset.Zero,
            state = rememberCollapsingTopBarState()
        )
    }
}