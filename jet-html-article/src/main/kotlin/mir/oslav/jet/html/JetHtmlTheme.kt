package mir.oslav.jet.html

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 28.08.2023
 */
internal class JetRippleTheme : RippleTheme {

    private val rippleAlpha = RippleAlpha(
        pressedAlpha = 0.32f,
        focusedAlpha = 0.32f,
        draggedAlpha = 0.24f,
        hoveredAlpha = 0.16f
    )

    @Composable
    override fun defaultColor(): Color {
        return MaterialTheme.colorScheme.onBackground
    }

    @Composable
    override fun rippleAlpha(): RippleAlpha {
        return rippleAlpha
    }
}