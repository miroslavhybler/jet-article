package mir.oslav.jet.html.composables.elements

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import mir.oslav.jet.html.data.HtmlElement


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
        model = ImageRequest.Builder(LocalContext.current)
            .data(data.url)
            .size(Size.ORIGINAL)
            .build()
    )

    val state = painter.state

    /*
    val transition by animateFloatAsState(
        targetValue = if (state is AsyncImagePainter.State.Success) 1f else 0f,
        label = "",
        animationSpec = tween(durationMillis = 4000)
    )
     */
    when (state) {
        is AsyncImagePainter.State.Success -> {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = modifier
             //       .alpha(transition)
             //       .scale(transition)
             //       .animateContentSize(
             //           animationSpec = tween(durationMillis = 800)
             //       )
            )
        }

        is AsyncImagePainter.State.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {

                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            }
        }

        is AsyncImagePainter.State.Error -> {

        }

        is AsyncImagePainter.State.Empty -> {

        }
    }

}