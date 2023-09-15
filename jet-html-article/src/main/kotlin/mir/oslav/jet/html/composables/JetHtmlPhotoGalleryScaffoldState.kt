@file:Suppress("MemberVisibilityCanBePrivate")

package mir.oslav.jet.html.composables

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.LocalHtmlDimensions
import mir.oslav.jet.utils.dpToPx
import mir.oslav.jet.utils.navigationBarsPadding
import mir.oslav.jet.utils.navigationBarsPaddingPx
import mir.oslav.jet.utils.pxToDp
import mir.oslav.jet.utils.statusBarsPadding
import mir.oslav.jet.utils.statusBarsPaddingPx
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 09.09.2023
 */
@Composable
internal fun JetHtmlPhotoGalleryScaffold(
    modifier: Modifier = Modifier,
    state: JetHtmlPhotoGalleryScaffoldState,
    galleryContent: @Composable () -> Unit,
    sheetContent: @Composable () -> Unit,
) {

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val statusBarPadding = density.statusBarsPaddingPx()
    val navigationBarPadding = density.navigationBarsPaddingPx()

    Layout(
        modifier = modifier.fillMaxSize(),
        content = {
            galleryContent()
            sheetContent()
        },
        measurePolicy = { measures, constraints ->
            val screenWidth = density.dpToPx(dp = configuration.screenWidthDp.dp).toInt()
            val screenHeight = density.dpToPx(dp = configuration.screenHeightDp.dp).toInt()

            val galleryMeasure = measures.getOrNull(index = 0) ?: throw NullPointerException("")
            val bottomSheetMeasure = measures.getOrNull(index = 1) ?: throw NullPointerException("")


            val galleryPlaceable = galleryMeasure.measure(
                constraints = constraints
            )
            val bottomSheetPlaceable = bottomSheetMeasure.measure(
                constraints = Constraints.fixedWidth(screenWidth)
            )


            layout(
                width = screenWidth,
                height = screenHeight + statusBarPadding,
                alignmentLines = emptyMap(),
                placementBlock = {

                    galleryPlaceable.place(x = 0, y = -statusBarPadding)

                    bottomSheetPlaceable.place(
                        x = 0,
                        y = screenHeight + navigationBarPadding - state.sheetHeight
                    )
                }
            )
        }
    )
}


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 12.09.2023
 */
data class JetHtmlPhotoGalleryScaffoldState internal constructor(
    val flingBehavior: FlingBehavior,
    val configuration: Configuration,
    val density: Density,
    val navigationBarPadding: Dp,
    val statusBarPadding: Dp,
    val dimensions: HtmlDimensions
)  {

    private val galleryDimensions get() = dimensions.gallery
    var totalScrollOffset: Offset by mutableStateOf(value = Offset.Zero)

    public val maxSheetHeight = configuration.screenHeightDp.dp
    public val minSheetHeight = galleryDimensions.sheetCollapsedHeight

    public val maxSheetHeightPx = density.dpToPx(dp = maxSheetHeight)
    public val minSheetHeightPx = density.dpToPx(dp = minSheetHeight)

    public var height: Dp by mutableStateOf(value = density.pxToDp(px = minSheetHeightPx))
    public val heightPx: Float get() = density.dpToPx(dp = height)

    //TODO set sheeet height from this class and make private setter
    var sheetHeight by mutableStateOf(
        value = density.dpToPx(dp = 75.dp + navigationBarPadding).toInt()
    )

}


@Composable
public fun rememberJetHtmlPhotoGalleryScaffoldState(
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    density: Density = LocalDensity.current,
    configuration: Configuration = LocalConfiguration.current,
    dimensions: HtmlDimensions = LocalHtmlDimensions.current
): JetHtmlPhotoGalleryScaffoldState {
    val navigationBarPadding = density.navigationBarsPadding()
    val statusBarPadding = density.statusBarsPadding()
    return remember {
        JetHtmlPhotoGalleryScaffoldState(
            flingBehavior = flingBehavior,
            density = density,
            configuration = configuration,
            navigationBarPadding = navigationBarPadding,
            statusBarPadding = statusBarPadding,
            dimensions = dimensions
        )
    }
}