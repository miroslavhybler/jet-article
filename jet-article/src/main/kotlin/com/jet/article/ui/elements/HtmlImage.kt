package com.jet.article.ui.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.imageLoader
import com.jet.article.R
import com.jet.article.data.HtmlElement


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 30.06.2023
 */
@Composable
fun HtmlImage(
    modifier: Modifier = Modifier,
    data: HtmlElement.Image,
    showErrorPlaceholder: Boolean = false,
    contentScale: ContentScale = ContentScale.FillWidth,
    shape: Shape = MaterialTheme.shapes.medium,
    loading: @Composable () -> Unit = { HtmlImageDefaults.LoadingLayout() },
    error: @Composable () -> Unit = { HtmlImageDefaults.ErrorLayout() },
) {

    HtmlImage(
        modifier = modifier,
        url = data.url,
        defaultSize = data.defaultSize,
        showErrorPlaceholder = showErrorPlaceholder,
        contentScale = contentScale,
        shape = shape,
        loading = loading,
        error = error,
    )
}


@Composable
fun HtmlImage(
    modifier: Modifier = Modifier,
    url: String,
    defaultSize: IntSize = IntSize.Zero,
    showErrorPlaceholder: Boolean = false,
    contentScale: ContentScale = ContentScale.FillWidth,
    shape: Shape = MaterialTheme.shapes.medium,
    loading: @Composable () -> Unit = { HtmlImageDefaults.LoadingLayout() },
    error: @Composable () -> Unit = { HtmlImageDefaults.ErrorLayout() },
) {
    val context = LocalContext.current

    SubcomposeAsyncImage(
        modifier = modifier
            .htmlImage(size = defaultSize)
            .clip(shape = shape),
        model = url,
        contentDescription = null,
        contentScale = contentScale,
        imageLoader = context.imageLoader,
        loading = { loading() },
        error = {
            if (showErrorPlaceholder) {
                error()
            }
        }
    )
}


private fun Modifier.htmlImage(size: IntSize): Modifier {
    return if (size != IntSize.Zero) {
        this.size(width = size.width.dp, height = size.height.dp)
    } else this
        .fillMaxWidth()
        .wrapContentHeight()
}


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 13.08.2024
 */
object HtmlImageDefaults {

    @Composable
    fun LoadingLayout() {
        CircularProgressIndicator(
            modifier = Modifier
                .size(size = 24.dp)
        )
    }

    @Composable
    fun ErrorLayout() {
        Icon(
            painter = painterResource(id = R.drawable.ic_jet_article_broken_image),
            contentDescription = null,
            modifier = Modifier
                .size(size = 32.dp),
            // .align(alignment = Alignment.Center),
            tint = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}