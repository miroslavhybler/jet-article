package com.jet.article.example.devblog

import androidx.activity.SystemBarStyle
import androidx.annotation.FloatRange
import androidx.appcompat.app.AppCompatDelegate
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.jet.article.example.devblog.data.Month
import com.jet.article.example.devblog.data.SettingsStorage
import com.jet.article.example.devblog.data.SimpleDate
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
    settings: SettingsStorage.Settings,
    lightScrim: Color = Color.Black,
    darkScrim: Color = Color.Black,
): SystemBarStyle {
    return rememberSystemBarsStyle(
        lightScrim = lightScrim,
        darkScrim = darkScrim,
        isAppDark = isAppDark(settings = settings),
    )
}


@Composable
fun isAppDark(settings: SettingsStorage.Settings): Boolean {
    val isSystemDarkMode = isSystemInDarkTheme()
    return remember(key1 = isSystemDarkMode, key2 = settings) {
        when (settings.nightModeFlags) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> isSystemDarkMode
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> false
        }
    }
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
            detectDarkMode = { resources ->
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
fun HtmlArticleData.getPostList(
    links: List<TagInfo>,
): Result<List<PostItem>> {
    try {
        val newList = ArrayList<HtmlElement>()
        newList.addAll(elements = elements)

        //Removing "featured" item
        newList.removeAt(index = 0)
        newList.removeAt(index = 0)
        newList.removeAt(index = 0)
        newList.removeAt(index = 0)

        val chunked = newList.chunked(size = 4)
        val list = chunked.mapIndexed { index, sublist ->
            val date = (sublist[2] as HtmlElement.TextBlock).text
            PostItem(
                image = (sublist[0] as HtmlElement.Image).url,
                title = (sublist[1] as HtmlElement.TextBlock).text,
                date = processDate(date = date)!!,
                description = (sublist[3] as HtmlElement.TextBlock).text,
                url = links[index].tagAttributes["href"]
                    ?: throw NullPointerException("Unable to extract href from ${links[index]}"),
            )
        }
        return Result.success(value = list)
    } catch (e: ClassCastException) {
        e.printStackTrace()
        return Result.failure(exception = e)
    } catch (e: NoSuchElementException) {
        e.printStackTrace()
        return Result.failure(exception = e)
    } catch (e: IndexOutOfBoundsException) {
        e.printStackTrace()
        return Result.failure(exception = e)
    }
}

fun processDate(
    date: String
): SimpleDate? {
    val array = date.split(' ')
    val day = array.getOrNull(index = 0)?.toIntOrNull()
    val monthString = array.getOrNull(index = 1)
    val month = Month.entries.find { it.displayName == monthString }
    val year = array.getOrNull(index = 2)?.toInt()


    return if (day != null && month != null && year != null) {
        SimpleDate(year = year, month = month, dayOfMonth = day)
    } else null
}


@Composable
fun rememberCurrentOffset(state: LazyListState): State<Int> {
    val position = remember { derivedStateOf { state.firstVisibleItemIndex } }
    val itemOffset = remember { derivedStateOf { state.firstVisibleItemScrollOffset } }
    val lastPosition = rememberPrevious(current = position.value)
    val lastItemOffset = rememberPrevious(current = itemOffset.value)
    val currentOffset = remember { mutableIntStateOf(value = 0) }

    LaunchedEffect(position.value, itemOffset.value) {
        if (lastPosition == null || position.value == 0) {
            currentOffset.intValue = itemOffset.value
        } else if (lastPosition == position.value) {
            currentOffset.intValue += (itemOffset.value - (lastItemOffset ?: 0))
        } else if (lastPosition > position.value) {
            currentOffset.intValue -= (lastItemOffset ?: 0)
        } else { // lastPosition.value < position.value
            currentOffset.intValue += itemOffset.value
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