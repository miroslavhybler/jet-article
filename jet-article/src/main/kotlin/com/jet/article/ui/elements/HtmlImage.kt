package com.jet.article.ui.elements

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.jet.article.data.HtmlElement
import kotlinx.coroutines.ensureActive
import mir.oslav.jet.html.article.R


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
    loading: @Composable BoxScope.() -> Unit = { HtmlImageDefaults.LoadingLayout(scope = this) },
    error: @Composable BoxScope.() -> Unit = { HtmlImageDefaults.ErrorLayout(scope = this) },
    diskCachePolicy: CachePolicy = CachePolicy.ENABLED,
    allowHardware: Boolean = true,
    onPainterReady: ((AsyncImagePainter) -> Unit)? = null,

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
        diskCachePolicy = diskCachePolicy,
        onPainterReady = onPainterReady,
        allowHardware=allowHardware,
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
    loading: @Composable BoxScope.() -> Unit = { HtmlImageDefaults.LoadingLayout(scope = this) },
    error: @Composable BoxScope.() -> Unit = { HtmlImageDefaults.ErrorLayout(scope = this) },
    diskCachePolicy: CachePolicy = CachePolicy.ENABLED,
    allowHardware: Boolean = true,
    onPainterReady: ((AsyncImagePainter) -> Unit)? = null,
) {
    val context = LocalContext.current

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context = context)
            .data(data = url)
            .size(size = Size.ORIGINAL)
            .diskCachePolicy(diskCachePolicy)
            .allowHardware(allowHardware)
            .diskCacheKey(key = if (diskCachePolicy == CachePolicy.ENABLED) url else null)
            .build()
    )


    LaunchedEffect(
        key1 = onPainterReady,
        key2 = painter.state,
    ) {
        if (onPainterReady != null && painter.state is AsyncImagePainter.State.Success) {
            onPainterReady(painter)
        }
    }

    Box(
        modifier = Modifier
            .then(other = modifier)
            .clip(shape = shape)
    ) {
        when (painter.state) {
            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .htmlImage(size = defaultSize)
                        .then(other = modifier),
                    contentScale = contentScale,
                )
            }

            is AsyncImagePainter.State.Loading -> {
                loading(this)
            }

            is AsyncImagePainter.State.Error -> {
                if (showErrorPlaceholder) {
                    error(this)
                }
            }

            is AsyncImagePainter.State.Empty -> Unit
        }
    }
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
data object HtmlImageDefaults {

    @Composable
    fun LoadingLayout(scope: BoxScope): Unit = with(scope) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(size = 24.dp)
                .align(alignment = Alignment.Center)
        )
    }

    @Composable
    fun ErrorLayout(scope: BoxScope): Unit = with(scope) {
        Icon(
            painter = painterResource(id = R.drawable.jet_article_broken_image),
            contentDescription = null,
            modifier = Modifier
                .size(size = 32.dp)
                .align(alignment = Alignment.Center),
            tint = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}