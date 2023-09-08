package mir.oslav.jet.html.composables

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import mir.oslav.jet.html.composables.topbars.AppearingTopBarState
import mir.oslav.jet.html.composables.topbars.CollapsingTopBarState
import kotlin.math.abs


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 07.09.2023
 */
class AppearingTopBarScrollConnection constructor(
    private val scaffoldState: JetHtmlArticleScaffoldState,
) : NestedScrollConnection {


    var scrollOffset = Offset.Zero
    private val topBarState: AppearingTopBarState
        get() {
            val state = scaffoldState.topBarState
            check(
                value = state is AppearingTopBarState,
                lazyMessage = {
                    "CollapsingTopBarScrollConnection cannot be used with ${state::class.simpleName}!"
                }
            )
            return state
        }

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        scrollOffset += available
        val topBarHeight = scaffoldState.topBarState.heightPx

        if (topBarHeight != 0f) {
            topBarState.backgroundAlpha = (abs(x = scrollOffset.y) / topBarHeight)
                .coerceIn(minimumValue = 0f, maximumValue = 1f)

            topBarState.elevation = (abs(x = scrollOffset.y) / topBarHeight)
                .coerceIn(minimumValue = 0f, maximumValue = 6f).dp
        }
        return super.onPreScroll(available = available, source = source)
    }
}

/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 07.09.2023
 */
class CollapsingTopBarScrollConnection constructor(
    private val scaffoldState: JetHtmlArticleScaffoldState,
) : NestedScrollConnection {

    private val topBarState: CollapsingTopBarState
        get() {
            val state = scaffoldState.topBarState
            check(
                value = state is CollapsingTopBarState,
                lazyMessage = {
                    "CollapsingTopBarScrollConnection cannot be used with ${state::class.simpleName}!"
                }
            )
            return state
        }


    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val dy = available.y
        val consumed = if (dy < 0) topBarState.dispatchRawDelta(delta = dy) else 0f
        val consumedOffset = Offset(0f, consumed)
        scaffoldState.totalScrollOffset += consumedOffset
        return consumedOffset
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        val dy = available.y
        return if (dy > 0) {
            Offset(0f, topBarState.dispatchRawDelta(delta = dy))
        } else {
            Offset(0f, 0f)
        }
    }
}


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 07.09.2023
 */
object EmptyScrollConnection : NestedScrollConnection