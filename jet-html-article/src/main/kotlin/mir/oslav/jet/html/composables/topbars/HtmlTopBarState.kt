package mir.oslav.jet.html.composables.topbars

import androidx.compose.ui.unit.Dp


/**
 * @author Miroslav Hýbler <br>
 * created on 07.09.2023
 */
interface HtmlTopBarState {


    abstract val minHeight: Dp

    abstract val minHeightPx: Float

    abstract val maxHeight: Dp

    abstract val maxHeightPx:Float


    abstract val height: Dp

    abstract val heightPx: Float
}