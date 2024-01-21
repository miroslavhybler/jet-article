package mir.oslav.jet.html.article.ui.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mir.oslav.jet.html.article.data.HtmlData


/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
@Composable
fun HtmlInvalid(
    modifier: Modifier = Modifier,
    error: HtmlData.Failure
) {

    Box(modifier = modifier.fillMaxSize()) {

        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = "Invalid",
                style = MaterialTheme.typography.titleLarge
            )
            Text(text = error.message)
            Text(text = error.cause.toString())
        }
    }
}