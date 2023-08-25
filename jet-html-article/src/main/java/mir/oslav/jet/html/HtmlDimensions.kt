package mir.oslav.jet.html

import android.content.res.Configuration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
data object HtmlDimensions {


    var sidePadding: Dp = 0.dp
        private set


    /**
     * @since 1.0.0
     */
    fun init(configuration: Configuration) {
        if (configuration.isLandScape) {
            when {
                configuration.isExtraLarge -> {
                    sidePadding = 32.dp
                }
                else -> {
                    sidePadding = 20.dp
                }
            }
        } else {
            when {
                configuration.isExtraLarge -> {
                    sidePadding = 20.dp
                }
                else -> {
                    sidePadding = 16.dp
                }
            }
        }
    }
}