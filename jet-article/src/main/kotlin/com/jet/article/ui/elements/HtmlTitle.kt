package com.jet.article.ui.elements

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.jet.article.data.HtmlElement


/**
 * @author Miroslav Hýbler <br>
 * created on 08.09.2023
 */
@Composable
fun HtmlTitle(
    modifier: Modifier = Modifier,
    title: HtmlElement.Title,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    val typography = MaterialTheme.typography

    val textStyle = remember(key1 = title) {
        when (title.titleTag) {
            "h1", "h2" -> typography.displaySmall.copy(color = color)
            else -> typography.titleLarge.copy(color = color)
        }
    }

    HtmlTextBlock(
        modifier = modifier,
        text = title.text,
        style = textStyle,
    )
}


@Composable
@Preview(showBackground = true)
private fun HtmlTitlePreview() {
    HtmlTitle(
        title = HtmlElement.Title(
            text = "Jetpack Compose rules!",
            titleTag = "h1",
            id = "page-title",
            key = 0,
        )
    )
}