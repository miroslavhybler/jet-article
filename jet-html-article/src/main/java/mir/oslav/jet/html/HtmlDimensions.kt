package mir.oslav.jet.html

import android.content.res.Configuration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
internal class HtmlDimensions constructor() {

    companion object {
        val empty: HtmlDimensions = HtmlDimensions()
    }


    //Total size o clickable icons
    val clickableIconSize: Dp = 42.dp


    //Inner padding of clickable icons (for ripple effect)
    val clickableIconPadding: Dp = 8.dp


    //Horizontal content padding from side of screen
    var sidePadding: Dp = 16.dp
        private set


    //Vertical content padding from top line of scaffold's content
    var topLinePadding: Dp = 32.dp
        private set


    //Vertical content padding from bottom line of scaffold's content
    var bottomLinePadding: Dp = 22.dp


    //Holding dimensions for CollapsingTopBar
    val collapsingTopBar: CollapsingTopBar = CollapsingTopBar()

    //
    val simpleTopBar: SimpleTopBar = SimpleTopBar()

    val table: Table = Table()


    /**
     * @since 1.0.0
     */
    //TODO init table, tobpar
    fun init(configuration: Configuration) {
        if (configuration.isLandScape) {
            setupLandscapeValues(configuration = configuration)
        } else {
            setupPortraitValues(configuration = configuration)
        }
    }


    /**
     * @since 1.0.0
     */
    private fun setupLandscapeValues(configuration: Configuration) {
        when {
            configuration.isExtraLarge -> {
                sidePadding = 32.dp
                topLinePadding = 42.dp
            }

            else -> {
                sidePadding = 20.dp
                topLinePadding = 16.dp
            }
        }
    }


    /**
     * @since 1.0.0
     */
    private fun setupPortraitValues(configuration: Configuration) {
        when {
            configuration.isExtraLarge -> {
                sidePadding = 20.dp
                topLinePadding = 42.dp

            }

            else -> {
                sidePadding = 16.dp
                topLinePadding = 24.dp
            }
        }
    }

    internal data class CollapsingTopBar constructor(
        var minHeight: Dp = 56.dp,
        var maxHeight: Dp = 226.dp,
    )

    internal data class SimpleTopBar constructor(
        var minHeight: Dp = 56.dp,
        var maxHeight: Dp = 56.dp,
    )


    internal data class Table constructor(
        val minCellWidth: Dp = 96.dp,
        val minCellHeight: Dp = 32.dp,

        val maxCellWidth: Dp = 192.dp,
        val maxCellHeight: Dp = 32.dp,
    )
}