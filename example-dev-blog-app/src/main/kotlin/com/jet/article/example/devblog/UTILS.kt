package com.jet.article.example.devblog

import androidx.activity.SystemBarStyle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.jet.article.ArticleParser
import com.jet.article.data.HtmlArticleData
import com.jet.article.example.devblog.data.ExcludeOption
import com.jet.article.example.devblog.ui.LocalDimensions
import com.jet.utils.pxToDp


/**
 * Modifier to adjust horizontal padding of component, handling gestures, content and different screen
 * orientations.
 * @author Miroslav Hýbler <br>
 * created on 12.08.2024
 */
fun Modifier.horizontalPadding(): Modifier = this.composed {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val dimensions = LocalDimensions.current
    val gesturePadding = WindowInsets.safeGestures
    val contentPadding = WindowInsets.safeContent

    val gestureLeft = gesturePadding.getLeft(density = density, layoutDirection = layoutDirection)
    val gestureRight = gesturePadding.getRight(density = density, layoutDirection = layoutDirection)
    val contentLeft = contentPadding.getLeft(density = density, layoutDirection = layoutDirection)
    val contentRight = contentPadding.getRight(density = density, layoutDirection = layoutDirection)
    val side = with(density) { dimensions.sidePadding.toPx().toInt() }

    //Final padding is from max value to make sure there is enough space for gestures or other
    //components (like front camera in landscape)
    val left = maxOf(a = gestureLeft, b = contentLeft, c = side)
    val right = maxOf(a = gestureRight, b = contentRight, c = side)

    this.padding(start = density.pxToDp(px = left), end = density.pxToDp(px = right))
}


/**
 * @author Miroslav Hýbler <br>
 * created on 11.07.2024
 */
val WindowWidthSizeClass.isCompat: Boolean
    get() = this == WindowWidthSizeClass.COMPACT

val WindowWidthSizeClass.isMedium: Boolean
    get() = this == WindowWidthSizeClass.MEDIUM

val WindowWidthSizeClass.isExpanded: Boolean
    get() = this == WindowWidthSizeClass.EXPANDED


val WindowHeightSizeClass.isCompat: Boolean
    get() = this == WindowHeightSizeClass.COMPACT

val WindowHeightSizeClass.isMedium: Boolean
    get() = this == WindowHeightSizeClass.MEDIUM

val WindowHeightSizeClass.isExpanded: Boolean
    get() = this == WindowHeightSizeClass.EXPANDED


@Composable
fun rememberSystemBarsStyle(
): SystemBarStyle {
    return rememberSystemBarsStyle(
        lightScrim = Color.Black,
        darkScrim = Color.Black,
    )
}


@Composable
fun rememberSystemBarsStyle(
    lightScrim: Color,
    darkScrim: Color,
    isAppDark: Boolean = isSystemInDarkTheme(),
): SystemBarStyle {
    return remember(key1 = isAppDark) {
        SystemBarStyle.auto(
            lightScrim = lightScrim.toArgb(),
            darkScrim = darkScrim.toArgb(),
            detectDarkMode = { resourrces ->
                isAppDark
            }
        )
    }
}


suspend fun ArticleParser.parseWithInitialization(
    content: String,
    url: String,
): HtmlArticleData {
    initialize(
        areImagesEnabled = true,
        isLoggingEnabled = true,
        isSimpleTextFormatAllowed = true,
    )
    ExcludeOption.devBlogExcludeRules.forEach { option ->
        addExcludeOption(
            tag = option.tag,
            clazz = option.clazz,
            id = option.id,
            keyword = option.keyword,
        )
    }

    return parse(content = content, url = url)
}