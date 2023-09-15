@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,

    )

package mir.oslav.jet.html.composables.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.JetRippleTheme
import mir.oslav.jet.html.LocalHtmlDimensions
import mir.oslav.jet.html.composables.JetHtmlPhotoGalleryScaffold
import mir.oslav.jet.html.composables.JetHtmlPhotoGalleryScaffoldState
import mir.oslav.jet.html.composables.elements.HtmlImage
import mir.oslav.jet.html.composables.rememberJetHtmlPhotoGalleryScaffoldState
import mir.oslav.jet.html.data.HtmlElement
import mir.oslav.jet.utils.dpToPx
import mir.oslav.jet.utils.navigationBarsPadding
import mir.oslav.jet.utils.navigationBarsPaddingPx
import mir.oslav.jet.utils.pxToDp
import mir.oslav.jet.utils.statusBarsPadding


/**
 * @author Miroslav Hýbler <br>
 * created on 28.08.2023
 */
@Composable
fun JetHtmlPhotoGalleryDetailScreen(
    modifier: Modifier = Modifier,
    gallery: HtmlElement.Constructed.Gallery,
    navHostController: NavHostController,

    ) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val statusBarHeight = density.statusBarsPadding()
    val navigationBarHeight = density.navigationBarsPadding()

    val dimensions = remember {
        HtmlDimensions().also { dimensions ->
            dimensions.init(
                configuration = configuration,
                screenWidth = configuration.screenWidthDp.dp,
                screenHeight = configuration.screenHeightDp.dp,
                statusBarHeight = statusBarHeight,
                navigationBarHeight = navigationBarHeight
            )
        }
    }

    val scaffoldState = rememberJetHtmlPhotoGalleryScaffoldState()
    val systemUiController = rememberSystemUiController()
    val coroutineScope = rememberCoroutineScope()


    val jetRippleTheme = remember { JetRippleTheme() }
    var imageScale: Float by remember { mutableFloatStateOf(value = 1f) }
    var imageOffsetY: Float by remember { mutableFloatStateOf(value = 0f) }

    val screenHeightPx = remember { density.dpToPx(dp = configuration.screenHeightDp.dp) }
    val bottomSheetHeightPx by remember { derivedStateOf { scaffoldState.sheetHeight } }
    var sheetElevation: Dp by remember { mutableStateOf(value = 0.dp) }

    val pagerState = rememberPagerState(initialPage = 0) {
        gallery.images.size
    }

    LaunchedEffect(
        key1 = bottomSheetHeightPx,
        block = {
            val adjSheetHeight = ((bottomSheetHeightPx - scaffoldState.minSheetHeightPx) * 0.4f)
            val adjScreenHeight = (screenHeightPx / 2f)
            val rawValue = 1f - ((adjSheetHeight / adjScreenHeight))
            imageScale = rawValue.coerceIn(minimumValue = 0.75f, maximumValue = 1f)
            imageOffsetY = -(density.dpToPx(dp = 256.dp) * (1f - imageScale))


            sheetElevation = (bottomSheetHeightPx / (screenHeightPx / 4) * 3.6f)
                .coerceIn(
                    minimumValue = 0f,
                    maximumValue = 4f
                ).dp
        }
    )

    LaunchedEffect(key1 = systemUiController, block = {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = true
        )
    })

    CompositionLocalProvider(
        LocalRippleTheme provides jetRippleTheme,
        LocalHtmlDimensions provides dimensions
    ) {
        JetHtmlPhotoGalleryScaffold(
            modifier = modifier,
            galleryContent = {
                HorizontalPagerGallery(
                    gallery = gallery,
                    modifier = Modifier.fillMaxSize(),
                    imageOffsetY = imageOffsetY,
                    imageScale = imageScale,
                    pagerState = pagerState
                )

            },
            sheetContent = {
                GridGallery(
                    gallery = gallery,
                    modifier = Modifier
                        .wrapContentSize()
                        .shadow(
                            elevation = sheetElevation,
                            ambientColor = MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        ),
                    state = scaffoldState,
                    onImageClick = { index ->
                        //TODO animate
                        coroutineScope.launch {
                            imageScale = 1f
                            imageOffsetY = 0f
                            val sheetHeight = dimensions.gallery.sheetCollapsedHeight
                            //TODO init sheet dimensions
                            scaffoldState.sheetHeight = density.dpToPx(dp = sheetHeight).toInt()
                            pagerState.animateScrollToPage(page = index)
                        }
                    }
                )
            },
            state = scaffoldState
        )
    }
}


//TODO support fling
@Composable
private fun GridGallery(
    modifier: Modifier = Modifier,
    gallery: HtmlElement.Constructed.Gallery,
    state: JetHtmlPhotoGalleryScaffoldState,
    onImageClick: (index: Int) -> Unit
) {
    val density = LocalDensity.current
    val navigationBarPadding = density.navigationBarsPadding()
    //TODO disable scroll when gallery is too small
    var isGalleryScrollEnabled by remember { mutableStateOf(value = true) }


    Box(
        modifier = modifier
            .height(height = density.pxToDp(px = state.sheetHeight))
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .pointerInput(Unit) {
                //TODO disable drag under the status bar
                detectDragGestures { change, dragAmount ->
                    isGalleryScrollEnabled = false
                    change.consume()
                    state.sheetHeight = (state.sheetHeight + (dragAmount.y * -1f))
                        .coerceIn(
                            minimumValue = state.minSheetHeightPx,
                            maximumValue = state.maxSheetHeightPx
                        )
                        .toInt()
                    isGalleryScrollEnabled = true
                }
            }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(height = 12.dp))

            Box(
                modifier = Modifier
                    .size(height = 4.dp, width = 32.dp)
                    .clip(shape = CircleShape)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.height(height = 12.dp))

            Text(
                text = "All Photos (${gallery.images.size})",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,

                )

            Spacer(modifier = Modifier.height(height = 24.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(count = 4),
                content = {
                    itemsIndexed(
                        items = gallery.images,
                        span = { index, image -> GridItemSpan(currentLineSpan = 1) },
                        itemContent = { index, image ->
                            HtmlImage(
                                data = image,
                                modifier = Modifier
                                    .padding(all = 4.dp)
                                    .fillMaxWidth()
                                    .aspectRatio(ratio = 1f)
                                    .clip(shape = RoundedCornerShape(size = 12.dp))
                                    .clickable(onClick = { onImageClick(index) })
                            )
                        }
                    )
                },
                userScrollEnabled = isGalleryScrollEnabled
            )
        }
    }
}


//TODO initial page
@Composable
private fun HorizontalPagerGallery(
    modifier: Modifier = Modifier,
    gallery: HtmlElement.Constructed.Gallery,
    imageScale: Float,
    imageOffsetY: Float,
    pagerState: PagerState
) {


    HorizontalPager(
        modifier = modifier
            .fillMaxSize()
            .padding(),
        state = pagerState,
    ) { pageIndex ->

        val image = gallery.images[pageIndex]

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .safeDrawingPadding()
        ) {
            HtmlImage(
                data = image,
                modifier = Modifier
                    .align(alignment = Alignment.Center)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .graphicsLayer(
                        translationY = imageOffsetY,
                        scaleX = imageScale,
                        scaleY = imageScale,
                        alpha = imageScale
                    )
            )
        }
    }
}