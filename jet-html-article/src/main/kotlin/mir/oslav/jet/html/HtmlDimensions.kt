package mir.oslav.jet.html

import android.content.res.Configuration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mir.oslav.jet.utils.isExtraLargeScreen
import mir.oslav.jet.utils.isLandScape


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
class HtmlDimensions constructor() {

    companion object {
        val empty: HtmlDimensions = HtmlDimensions()
    }


    //Horizontal content padding from side of screen
    var sidePadding: Dp = 16.dp
        private set


    //Vertical content padding from top line of scaffold's content
    var topLinePadding: Dp = 32.dp
        private set


    //Vertical extra content padding from bottom line of scaffold's content
    var baseLinePadding: Dp = 22.dp


    //TODO  table cells size
    val table: Table = Table()


    /**
     * @since 1.0.0
     */
    //TODO init table
    fun init(
        configuration: Configuration,
        screenWidth: Dp,
        screenHeight: Dp,
    ) {
        if (configuration.isLandScape) {
            setupLandscapeValues(
                configuration = configuration,
                screenWidth = screenWidth,
                screenHeight = screenHeight,
            )
        } else {
            setupPortraitValues(
                configuration = configuration,
                screenWidth = screenWidth,
                screenHeight = screenHeight,
            )
        }
    }


    /**
     * @since 1.0.0
     */
    private fun setupLandscapeValues(
        configuration: Configuration,
        screenWidth: Dp,
        screenHeight: Dp,
    ) {
        when {
            configuration.isExtraLargeScreen -> {
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
    private fun setupPortraitValues(
        configuration: Configuration,
        screenWidth: Dp,
        screenHeight: Dp,
    ) {
        when {
            configuration.isExtraLargeScreen -> {
                sidePadding = 20.dp
                topLinePadding = 42.dp

            }

            else -> {
                sidePadding = 16.dp
                topLinePadding = 24.dp
            }
        }
    }



    /**
     * @since 1.0.0
     */
    data class Table internal constructor(
        val minCellWidth: Dp = 96.dp,
        val minCellHeight: Dp = 32.dp,

     //   val maxCellWidth: Dp = 192.dp,
     //   val maxCellHeight: Dp = 32.dp,
    )
}