package com.jet.article.ui.elements

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jet.article.data.HtmlElement
import com.jet.article.ui.LocalColorScheme


/**
 * @author Miroslav Hýbler <br>
 * created on 08.09.2023
 */
@Composable
fun HtmlTitle(
    modifier: Modifier = Modifier,
    title: HtmlElement.Title
) {
    val typography = MaterialTheme.typography
    val colorScheme = LocalColorScheme.current

    val textStyle = remember(key1 = title) {
        when (title.titleTag) {
            "h1", "h2" -> typography.displaySmall.copy(color = colorScheme.textColor)
            else -> typography.titleLarge.copy(color = colorScheme.textColor)
        }
    }

    HtmlTextBlock(
        text = title.text,
        style = textStyle,
        modifier = modifier.padding(
            top = when (title.titleTag) {
                "h1", "h2" -> 24.dp
                else -> 16.dp
            }
        )
    )
}


@Composable
@Preview(showBackground = true)
private fun HtmlTitlePreview() {
    HtmlTitle(
        title = HtmlElement.Title(
            text = "Jetpack Compose rules!",
            titleTag = "h1",
            id = ""
        )
    )
}