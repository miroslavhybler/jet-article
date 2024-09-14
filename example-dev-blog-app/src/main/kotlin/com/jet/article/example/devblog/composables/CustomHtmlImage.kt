package com.jet.article.example.devblog.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.compose.AsyncImagePainter
import com.jet.article.data.HtmlElement
import com.jet.article.ui.elements.HtmlImage
import com.jet.article.ui.elements.HtmlImageDefaults


/**
 * @author Miroslav Hýbler <br>
 * created on 26.08.2024
 */
@Composable
fun CustomHtmlImageWithPalette(
    modifier: Modifier = Modifier,
    image: HtmlElement.Image,
    onPallete: ((Palette) -> Unit),
) {

    HtmlImage(
        modifier = modifier.animateContentSize(),
        data = image,
        allowHardware = false,
        onPainterReady = { painter ->
            val bitmap = (painter.state as AsyncImagePainter.State.Success)
                .result.drawable.toBitmap()
            Palette.from(bitmap).generate() { palette ->
                if (palette != null) {
                    onPallete(palette)
                }
            }
        },
        loading = {
            CustomHtmlImageDefaults.Loading()
        },
        error = { HtmlImageDefaults.ErrorLayout(scope = this) }
    )
}

@Composable
fun CustomHtmlImage(
    modifier: Modifier = Modifier,
    image: HtmlElement.Image,
) {
    HtmlImage(
        modifier = modifier.animateContentSize(),
        data = image,
        loading = {
            CustomHtmlImageDefaults.Loading()
        },
        error = { HtmlImageDefaults.ErrorLayout(scope = this) }
    )
}

@Composable
fun CustomHtmlImage(
    modifier: Modifier = Modifier,
    url: String,
) {

    HtmlImage(
        modifier = modifier.animateContentSize(),
        url = url,
        loading = {
            CustomHtmlImageDefaults.Loading()
        },
        error = { HtmlImageDefaults.ErrorLayout(scope = this) }
    )
}



object CustomHtmlImageDefaults {

    @Composable
    fun Loading() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 128.dp)
                .background(
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.medium,
                )
        )
    }
}