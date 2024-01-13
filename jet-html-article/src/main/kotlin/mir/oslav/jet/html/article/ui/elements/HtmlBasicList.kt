package mir.oslav.jet.html.article.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mir.oslav.jet.html.article.data.HtmlElement


/**
 * @author Miroslav Hýbler <br>
 * created on 13.01.2024
 */
@Composable
fun HtmlBasicList(
    modifier: Modifier = Modifier,
    list: HtmlElement.BasicList
) {

    Column(modifier = modifier) {
        list.items.forEachIndexed { index, s ->
            Text(
                text = if (list.isOrdered) "${index + 1} $s" else s,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}