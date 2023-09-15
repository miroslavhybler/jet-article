package mir.oslav.jet.html.composables

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import mir.oslav.jet.html.LocalHtmlDimensions
import mir.oslav.jet.html.data.HtmlConfig
import mir.oslav.jet.html.composables.topbars.CollapsingTopBarState
import mir.oslav.jet.html.composables.topbars.HtmlTopBarState
import mir.oslav.jet.utils.dpToPx
import mir.oslav.jet.utils.statusBarsPaddingPx


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 02.09.2023
 */
@Composable
internal fun JetHtmlArticleScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    content: @Composable BoxScope.() -> Unit,
    config: HtmlConfig,
    scaffoldState: JetHtmlArticleScaffoldState,
) {

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val nestedScrollConnection = remember {
        config.topBarConfig.createScrollConnection(scaffoldState = scaffoldState)
    }

    val statusBarSize = density.statusBarsPaddingPx()

    Layout(
        content = {
            topBar()
            Box(
                modifier = modifier,
                content = content
            )
        },
        modifier = modifier.nestedScroll(connection = nestedScrollConnection),
    ) { measures, constraints ->

        val topBarConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        val topBarMeasure = measures.getOrNull(index = 0) ?: throw NullPointerException("TODO")
        val bodyMeasure = measures.getOrNull(index = 1) ?: throw NullPointerException("TODO")

        val bodyPlaceable = bodyMeasure.measure(constraints = constraints)
        val topBarPlaceable = topBarMeasure.measure(constraints = topBarConstraints)

        layout(
            width = with(density) { configuration.screenWidthDp.dp.toPx() }.toInt(),
            height = with(density) { configuration.screenHeightDp.dp.toPx() }.toInt(),
            alignmentLines = emptyMap(),
            placementBlock = {

                //TODO place body under topbar when tobarPlaceable.height <= min height

                val topBarMinHeight = scaffoldState.topBarState.minHeightPx

                val bodyY = if (topBarPlaceable.height <= topBarMinHeight) {
                    val scrollY = scaffoldState.totalScrollOffset.y.toInt()
                    //  (topBarMinHeight - (scrollY + bodyPlaceable.height)).toInt()
                    0
                } else topBarPlaceable.height

                bodyPlaceable.placeRelative(x = 0, y = topBarPlaceable.height - statusBarSize)
                topBarPlaceable.place(x = 0, y = -statusBarSize)
            }

        )
    }
}


/**
 * @since 1.0.0
 */
data class JetHtmlArticleScaffoldState internal constructor(
    val topBarState: HtmlTopBarState,
    val flingBehavior: FlingBehavior,
    val config: HtmlConfig,
) {

    var totalScrollOffset: Offset by mutableStateOf(value = Offset.Zero)
}


/**
 * @since 1.0.0
 */
@Composable
fun rememberJetHtmlArticleScaffoldState(
    config: HtmlConfig,
    topBarState: CollapsingTopBarState = CollapsingTopBarState(
        density = LocalDensity.current,
        topBarDimens = LocalHtmlDimensions.current.collapsingTopBar
    ),
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior()
): JetHtmlArticleScaffoldState = remember {
    JetHtmlArticleScaffoldState(
        topBarState = topBarState,
        config = config,
        flingBehavior = flingBehavior
    )
}