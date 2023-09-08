@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,

    )

package mir.oslav.jet.html.composables.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import mir.oslav.jet.html.composables.elements.HtmlImage
import mir.oslav.jet.html.data.HtmlElement


/**
 * @author Miroslav Hýbler <br>
 * created on 28.08.2023
 */
@Composable
fun JetHtmlPhotoGalleryDetailScreen(
    modifier: Modifier = Modifier,
    gallery: HtmlElement.Constructed.Gallery,
    navHostController: NavHostController
) {

    val coroutineScope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(key1 = systemUiController, block = {
        systemUiController.setSystemBarsColor(color = Color.Transparent, darkIcons = false)
    })

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            initialValue = SheetValue.Expanded,
            skipPartiallyExpanded = true,
        )
    )

    Scaffold(

    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {

            Box(modifier = modifier) {
                HorizontalPagerGallery(
                    gallery = gallery,
                    paddingValues = paddingValues
                )
            }

            GridGallery(
                gallery = gallery,
                modifier = Modifier.align(alignment = Alignment.BottomCenter)
            )

        }
    }


}


@Composable
private fun GridGallery(
    modifier: Modifier = Modifier,
    gallery: HtmlElement.Constructed.Gallery
) {
    val density = LocalDensity.current
    var height by remember { mutableStateOf(value = 56.dp) }
    var isGalleryScrollEnabled by remember { mutableStateOf(value = true) }
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val anchors = mapOf(0f to 0, with(density) { height.toPx() } to 1)
    val coroutineScope = rememberCoroutineScope()


    val nestedScrollConnection = remember {
        object : NestedScrollConnection {

        }
    }

    Box(
        modifier = modifier
            .height(height = height)
            .background(color = MaterialTheme.colorScheme.background)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Vertical
            )
            .pointerInput(key1 = Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    coroutineScope.launch {
                        swipeableState.performDrag(delta = dragAmount.y)
                        height += with(density) {
                            (dragAmount.y * -1).toDp()
                        }
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier
        ) {

            Spacer(modifier = Modifier.height(height = 12.dp))

            Box(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .size(height = 4.dp, width = 32.dp)
                    .clip(shape = CircleShape)
                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            isGalleryScrollEnabled = false
                            change.consume()
                            height += with(density) {
                                (dragAmount.y * -1).toDp()
                            }
                            isGalleryScrollEnabled = true
                        }
                    }
            )

            Spacer(modifier = Modifier.height(height = 36.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(count = 5),
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
    paddingValues: PaddingValues
) {
    val pagerState = rememberPagerState(initialPage = 0) {
        gallery.images.size
    }

    HorizontalPager(
        modifier = modifier
            .fillMaxSize()
            .padding(),
        state = pagerState,
    ) { pageIndex ->

        val image = gallery.images[pageIndex]
        HtmlImage(
            data = image,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}