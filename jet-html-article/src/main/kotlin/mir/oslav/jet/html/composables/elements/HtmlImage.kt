package mir.oslav.jet.html.composables.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import mir.oslav.jet.html.HtmlDataSamples
import mir.oslav.jet.html.R
import mir.oslav.jet.html.data.HtmlElement


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 30.06.2023
 */
@Composable
fun HtmlImage(
    modifier: Modifier = Modifier,
    data: HtmlElement.Parsed.Image
) {

    val isInspectionMode = LocalInspectionMode.current


    /*
    val transition by animateFloatAsState(
        targetValue = if (state is AsyncImagePainter.State.Success) 1f else 0f,
        label = "",
        animationSpec = tween(durationMillis = 4000)
    )
     */

    if (isInspectionMode) {
        Image(
            painter = painterResource(id = HtmlDataSamples.images.random()),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .then(other = modifier),
            contentScale = ContentScale.Crop
        )
    } else {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(data = data.url)
                .size(size = Size.ORIGINAL)
                .build()
        )
        when (painter.state) {
            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = modifier,
                    contentScale = ContentScale.Crop
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Column {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_jet_html_error_image),
                            contentDescription = null,
                            modifier = Modifier.size(size = 24.dp),
                            tint = MaterialTheme.colorScheme.error
                        )

                        Text(
                            text = "Unable to load image",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            is AsyncImagePainter.State.Empty -> {

            }
        }
    }

}