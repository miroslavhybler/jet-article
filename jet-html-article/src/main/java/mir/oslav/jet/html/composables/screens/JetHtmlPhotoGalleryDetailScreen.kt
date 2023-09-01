@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,

    )

package mir.oslav.jet.html.composables.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import mir.oslav.jet.html.composables.elements.images.HtmlImage
import mir.oslav.jet.html.data.HtmlElement


/**
 * @author Miroslav Hýbler <br>
 * created on 28.08.2023
 */
@Composable
fun JetHtmlPhotoGalleryDetailScreen(
    modifier: Modifier = Modifier,
    gallery: HtmlElement.Gallery,
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
    gallery: HtmlElement.Gallery
) {
    LazyVerticalGrid(
        modifier=modifier,
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
        }
    )
}


//TODO initial page
@Composable
private fun HorizontalPagerGallery(
    modifier: Modifier = Modifier,
    gallery: HtmlElement.Gallery,
    paddingValues: PaddingValues
) {
    val pagerState = rememberPagerState(initialPage = 0) {
        gallery.images.size
    }

    HorizontalPager(
        modifier = Modifier
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