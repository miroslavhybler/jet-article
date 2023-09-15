package mir.oslav.jet.html.composables.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import mir.oslav.jet.html.LocalHtmlDimensions
import mir.oslav.jet.html.data.HtmlElement


/**
 * @author Miroslav Hýbler <br>
 * created on 08.09.2023
 */
@Composable
fun HtmlTitle(
    modifier: Modifier = Modifier,
    title: HtmlElement.Parsed.Title
) {

    val dimensions = LocalHtmlDimensions.current
    val typography = MaterialTheme.typography

    val textStyle = remember(key1 = title) {
        when (title.titleTag) {
            "h1" -> typography.displayLarge
            "h2" -> typography.displayMedium
            "h3" -> typography.displaySmall
            "h4" -> typography.headlineLarge
            "h5" -> typography.headlineMedium
            "h6" -> typography.headlineSmall
            "h7" -> typography.titleLarge
            else -> throw IllegalStateException("Tag ${title.titleTag} is not supported!")
        }
    }


    Text(
        text = title.text,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.sidePadding),
        style = textStyle
    )
}


@Composable
@Preview
private fun HtmlTitlePreview() {
    HtmlTitle(
        title = HtmlElement.Parsed.Title(
            text = "Jetpack Compose rules!",
            startIndex = 0,
            endIndex = 0,
            titleTag = "h1",
            span = 1
        )
    )
}