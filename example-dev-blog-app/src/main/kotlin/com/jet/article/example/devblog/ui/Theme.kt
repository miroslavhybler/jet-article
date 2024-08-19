@file:Suppress(
    "RedundantVisibilityModifier", "DataClassPrivateConstructor",
    "MemberVisibilityCanBePrivate", "ConstPropertyName"
)

package com.jet.article.example.devblog.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jet.article.example.devblog.isCompat
import com.jet.article.example.devblog.isExpanded
import com.jet.article.example.devblog.isMedium

private val Green80: Color = Color(color = 0xFF13B041)
private val Green90: Color = Color(color = 0xFF07882B)
private val Green40: Color = Color(color = 0xFF3ddc84)

val LightColorScheme: ColorScheme = lightColorScheme(
    primary = Green80,
    onPrimary = Color.White,
    primaryContainer = Green90,
    onPrimaryContainer = Color.White,
    secondary = Color(color = 0xFF08677f),
    onSecondary = Color(color = 0xFFffffff),
    secondaryContainer = Color(color = 0xFFb7eaff),
    onSecondaryContainer = Color(color = 0xFF001f28),
    error = Color.Red,
    onError = Color.White,
    errorContainer = Color.Red,
    onErrorContainer = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(color = 0xFFE7E0EC),
    onSurfaceVariant = Color(color = 0xFF49454F),
    outline = Color(color = 0xFF79747E),
)


val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Color.Black,
    primaryContainer = Green90,
    onPrimaryContainer = Color.Black,
    secondary = Color(color = 0xFF88d1ec),
    onSecondary = Color(color = 0xFF003544),
    secondaryContainer = Color(color = 0xFF004d61),
    onSecondaryContainer = Color(color = 0xFFb7eaff),
    tertiary = Green80,
    onTertiary = Color.Black,
    tertiaryContainer = Green90,
    onTertiaryContainer = Color.Black,
    error = Color.Red,
    onError = Color.Black,
    errorContainer = Color.Red,
    onErrorContainer = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    surfaceVariant = Color(color = 0xFF49454F),
    onSurfaceVariant = Color(color = 0xFFCAC4D0),
    outline = Color(color = 0xFF938F99),
)

@Composable
fun DevBlogAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}

object DevBlogAppTheme {

    val colorAndroid: Color
        get() = Green40
}


/**
 * @param topLinePadding Extra content padding between topBar component and content.
 * @param bottomLinePadding Extra content padding between bottomBar component and content.
 * Is also used as extra bottom padding in scrollable containers too.
 * @param sidePadding Horizontal content padding from window edge
 * @see <a href="https://developer.android.com/develop/ui/compose/layouts/adaptive/window-size-classes">WindowSizeClass</a>
 * @author Miroslav Hýbler <br>
 * created on 04.11.2023
 */
public data class Dimensions private constructor(
    val topLinePadding: Dp,
    val bottomLinePadding: Dp,
    val sidePadding: Dp,
) {
    companion object {
        public val default: Dimensions = Dimensions(
            sidePadding = 16.dp,
            bottomLinePadding = 16.dp,
            topLinePadding = 24.dp,
        )
        public val landscape: Dimensions = Dimensions(
            sidePadding = 20.dp,
            bottomLinePadding = 16.dp,
            topLinePadding = 12.dp,
        )
        public val tablet: Dimensions = Dimensions(
            sidePadding = 32.dp,
            bottomLinePadding = 20.dp,
            topLinePadding = 28.dp,
        )


        /**
         * @see <a href="https://developer.android.com/develop/ui/compose/layouts/adaptive/window-size-classes">WindowSizeClass</a>
         */
        fun getForWindow(info: WindowAdaptiveInfo): Dimensions {
            val width = info.windowSizeClass.windowWidthSizeClass
            val height = info.windowSizeClass.windowHeightSizeClass

            if (width.isMedium || width.isExpanded) {
                //Probably landscape or opened flip
                return when {
                    //Tablet landscape
                    height.isMedium -> tablet
                    //Phone landscape
                    height.isCompat -> landscape
                    else -> default
                }
            }

            //Default, phone portrait
            return default
        }
    }
}

@Composable
fun rememberDimensions(): Dimensions {
    val windowInfo = currentWindowAdaptiveInfo()
    return remember(key1 = windowInfo) {
        Dimensions.getForWindow(info = windowInfo)
    }
}


/**
 * Local provider for dimensions, should be applied in top level composable functions
 */
val LocalDimensions: ProvidableCompositionLocal<Dimensions> = compositionLocalOf(
    defaultFactory = Dimensions.Companion::default,
    )



data object Routes {
    const val main: String ="main"
    const val settings: String ="settings"
}