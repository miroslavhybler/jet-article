package com.jet.article.example.devblog

import androidx.activity.SystemBarStyle
import androidx.annotation.FloatRange
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.jet.article.data.HtmlElement
import com.jet.article.data.TagInfo
import com.jet.article.example.devblog.data.ExcludeOption
import com.jet.article.example.devblog.ui.LocalDimensions
import com.jet.article.example.devblog.data.database.PostItem
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
        isLoggingEnabled = false,
        isSimpleTextFormatAllowed = true,
        isQueringTextOutsideTextTags = true,
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


/**
 *
 */
//TODO add support for featured item
fun HtmlArticleData.getPostList(
    links: List<TagInfo>,
): List<PostItem> {
    try {
        val newList = ArrayList<HtmlElement>()
        newList.addAll(elements = elements)

        //Removing "featured" item
        newList.removeAt(0)
        newList.removeAt(0)
        newList.removeAt(0)
        newList.removeAt(0)

        val chunked = newList.chunked(size = 4)
        val list = chunked.mapIndexed { index, sublist ->
            PostItem(
                image = (sublist[0] as HtmlElement.Image).url,
                title = (sublist[1] as HtmlElement.TextBlock).text,
                time = (sublist[2] as HtmlElement.TextBlock).text,
                description = (sublist[3] as HtmlElement.TextBlock).text,
                url = links[index].tagAttributes["href"]
                    ?: throw NullPointerException("Unable to extract href from ${links[index]}"),
            )
        }
        return list
    } catch (e: ClassCastException) {
        e.printStackTrace()
        return emptyList()
    } catch (e: NoSuchElementException) {
        e.printStackTrace()
        return emptyList()
    }

}


@Composable
fun rememberCurrentOffset(state: LazyListState): State<Int> {
    val position = remember { derivedStateOf { state.firstVisibleItemIndex } }
    val itemOffset = remember { derivedStateOf { state.firstVisibleItemScrollOffset } }
    val lastPosition = rememberPrevious(current = position.value)
    val lastItemOffset = rememberPrevious(current = itemOffset.value)
    val currentOffset = remember { mutableStateOf(value = 0) }

    LaunchedEffect(position.value, itemOffset.value) {
        if (lastPosition == null || position.value == 0) {
            currentOffset.value = itemOffset.value
        } else if (lastPosition == position.value) {
            currentOffset.value += (itemOffset.value - (lastItemOffset ?: 0))
        } else if (lastPosition > position.value) {
            currentOffset.value -= (lastItemOffset ?: 0)
        } else { // lastPosition.value < position.value
            currentOffset.value += itemOffset.value
        }
    }

    return currentOffset
}

@Composable
fun <T> rememberPrevious(
    current: T,
    shouldUpdate: (prev: T?, curr: T) -> Boolean = { a: T?, b: T -> a != b },
): T? {
    val ref = rememberRef<T>()

    // launched after render, so the current render will have the old value anyway
    SideEffect {
        if (shouldUpdate(ref.value, current)) {
            ref.value = current
        }
    }

    return ref.value
}


/**
 * Returns a dummy MutableState that does not cause render when setting it
 */
@Composable
fun <T> rememberRef(): MutableState<T?> {
    // for some reason it always recreated the value with vararg keys,
    // leaving out the keys as a parameter for remember for now
    return remember() {
        object : MutableState<T?> {
            override var value: T? = null

            override fun component1(): T? = value

            override fun component2(): (T?) -> Unit = { value = it }
        }
    }
}