package com.jet.article.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.jet.article.data.HtmlElement
import mir.oslav.jet.html.article.R


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 30.06.2023
 */
@Composable
fun HtmlImage(
    modifier: Modifier = Modifier,
    data: HtmlElement.Image
) {

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(data = data.url)
            .size(size = Size.ORIGINAL)
            .build()
    )
    when (painter.state) {
        //TODO animate?
        is AsyncImagePainter.State.Success -> {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .then(other = modifier),
                contentScale = ContentScale.Crop
            )
        }

        is AsyncImagePainter.State.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .then(other = modifier)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(size = 24.dp)
                        .align(alignment = Alignment.Center)
                )


            }
        }

        is AsyncImagePainter.State.Error -> {
            PhotoError()
        }

        is AsyncImagePainter.State.Empty -> {

        }
    }
}


@Composable
private fun PhotoError() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.errorContainer)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_broken_image),
            contentDescription = null,
            modifier = Modifier
                .size(size = 32.dp)
                .align(alignment = Alignment.Center),
            tint = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}
