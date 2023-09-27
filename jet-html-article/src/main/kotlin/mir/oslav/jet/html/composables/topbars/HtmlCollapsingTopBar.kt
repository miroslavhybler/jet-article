package mir.oslav.jet.html.composables.topbars

import android.util.Log
import androidx.annotation.FloatRange
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
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
import mir.oslav.jet.utils.pxToDp
import mir.oslav.jet.utils.statusBarsPadding
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 27.08.2023
 */
@Composable
fun HtmlCollapsingTopBar(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    title: String,
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


    val maxTitleOffset = dimensions.collapsingTopBar.maxHeight - (textHeight * 2) - 16.dp
    val maxTitleOffsetPx = with(density) { maxTitleOffset.toPx() }


    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(
                min = dimensions.collapsingTopBar.minHeight,
                max = dimensions.collapsingTopBar.maxHeight
            )
            .shadow(
                elevation = 4.dp,
                spotColor = MaterialTheme.colorScheme.onBackground,
                shape = RectangleShape
            )
            .background(color = MaterialTheme.colorScheme.background)
            .padding(
                start = dimensions.sidePadding,
                end = dimensions.sidePadding,
                top = density.statusBarsPadding()
            )
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

        Box(
            modifier = Modifier
                .align(alignment = Alignment.Center)
                .fillMaxWidth()
                .height(height = state.height)
        ) {

            val paddingFromIcon = dimensions.clickableIconSize * (state.progress)

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = if (state.progress == 1f) 1 else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(alignment = Alignment.Center)
                    .padding(
                        start = 8.dp,
                        end = dimensions.sidePadding + paddingFromIcon
                    )
                    .offset(
                        x = paddingFromIcon,
                        y = 44.dp * (1f - state.progress)
                    )
                    .fillMaxWidth()

            )
        }
    }
}


/**
 * @since 1.0.0
 */
class CollapsingTopBarState internal constructor(
    topBarDimens: HtmlDimensions.CollapsingTopBar,
    val density: Density,
) : HtmlTopBarState, ScrollableState {

    override val maxHeight = topBarDimens.maxHeight
    override val minHeight = topBarDimens.minHeight
    override val maxHeightPx = with(density) { maxHeight.toPx() }
    override val minHeightPx = with(density) { minHeight.toPx() }

    private var threshold: Float = 0f


    override var height: Dp by mutableStateOf(value = maxHeight)
    override val heightPx: Float get() = with(density) { height.toPx() }


    /**
     * Progress of collapsing, 0f when topBar has max height, 1f when collapse is complete and topBar
     * has min height
     * @since 1.0.0
     */
    @FloatRange(from = 0.0, to = 1.0)
    var progress: Float = 0f
        private set

    override val isScrollInProgress: Boolean
        get() = scrollableState.isScrollInProgress


    private val scrollableState = ScrollableState { value ->
        val toConsume = if (value < 0) {
            max(a = minHeightPx - heightPx, b = value)
        } else {
            min(a = maxHeightPx - heightPx, b = value)
        }

        val current = toConsume + threshold
        val currentInt = current.toInt()

        if (current.absoluteValue > 0) {
            height += density.pxToDp(px = currentInt)
            threshold = current - currentInt
        }

        val ratio = (heightPx - minHeightPx) / (maxHeightPx - minHeightPx)
        val rawProgress = 1f - ratio
        val coercesProgress = rawProgress.coerceIn(
            minimumValue = 0f,
            maximumValue = 1f
        )

        progress = when {
            coercesProgress < 0.007f -> 0f
            coercesProgress > 0.993 -> 1f
            else -> coercesProgress
        }
        toConsume
    }


    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ) {
        scrollableState.scroll(scrollPriority = scrollPriority, block = block)
    }

    override fun dispatchRawDelta(delta: Float): Float {
        return scrollableState.dispatchRawDelta(delta = delta)
    }


    suspend fun fling(
        flingBehavior: FlingBehavior,
        velocity: Float
    ): Float {
        var left = velocity
        scroll {
            with(flingBehavior) {
                left = performFling(left)
            }
        }

        return left
    }
}


/**
 * @since 1.0.0
 */
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


/**
 * @since 1.0.0
 */
@Composable
@Preview(showBackground = true)
private fun SimplePreview() {

    Column {
        HtmlCollapsingTopBar(
            title = "Build better apps faster with Jetpack Compose and androidX",
            navHostController = rememberNavController(),
            state = rememberCollapsingTopBarState()
        )

        HtmlCollapsingTopBar(
            title = "Build better apps faster with Jetpack Compose and androidX",
            navHostController = rememberNavController(),
            state = rememberCollapsingTopBarState()
        )
    }
}