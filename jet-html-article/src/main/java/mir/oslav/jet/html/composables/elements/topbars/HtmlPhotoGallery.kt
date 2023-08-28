package mir.oslav.jet.html.composables.elements.topbars

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import mir.oslav.jet.html.HtmlDimensions
import mir.oslav.jet.html.composables.elements.HtmlImage
import mir.oslav.jet.html.data.HtmlElement


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 28.08.2023
 */
@Composable
fun HtmlPhotoGallery(
    modifier: Modifier = Modifier,
    gallery: HtmlElement.Gallery
) {

    if (gallery.images.isEmpty()) {
        //TODO empty gallery
        return
    }

    Column(
        modifier = modifier
            .padding(horizontal = HtmlDimensions.sidePadding)
    ) {
        HtmlImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 256.dp)
                .clip(shape = RoundedCornerShape(size = 24.dp))
                .clickable(
                    onClick = {
                        //TODO open detail
                    }
                ),
            data = gallery.images.first()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            for (i in 1 until gallery.images.size) {
                if (i == 4) {
                    break
                }
                val image = gallery.images[i]
                HtmlImage(
                    modifier = Modifier
                        .weight(weight = 1f)
                        .height(height = 86.dp)
                        .clip(shape = RoundedCornerShape(size = 12.dp))
                        .clickable(
                            onClick = {
                                //TODO open detail
                            }
                        ),
                    data = image
                )

                if (i != 3) {
                    Spacer(modifier = Modifier.width(width = 8.dp))
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
private fun HtmlGalleryPreview() {

    HtmlPhotoGallery(
        gallery = HtmlElement.Gallery(
            span = 1,
            images = listOf(
                HtmlElement.Image(
                    span = 1,
                    startIndex = Int.MIN_VALUE,
                    endIndex = Int.MIN_VALUE,
                    url = "",
                    description = null
                ),
                HtmlElement.Image(
                    span = 1,
                    startIndex = Int.MIN_VALUE,
                    endIndex = Int.MIN_VALUE,
                    url = "",
                    description = null
                ),
                HtmlElement.Image(
                    span = 1,
                    startIndex = Int.MIN_VALUE,
                    endIndex = Int.MIN_VALUE,
                    url = "",
                    description = null
                ),
                HtmlElement.Image(
                    span = 1,
                    startIndex = Int.MIN_VALUE,
                    endIndex = Int.MIN_VALUE,
                    url = "",
                    description = null
                )
            )
        )
    )
}