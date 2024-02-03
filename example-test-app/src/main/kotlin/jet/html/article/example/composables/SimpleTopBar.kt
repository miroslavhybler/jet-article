package jet.html.article.example.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


/**
 * @author Miroslav Hýbler <br>
 * created on 29.01.2024
 */
@Composable
fun SimpleTopaBar(modifier: Modifier = Modifier, text: String = "Jet Article Dev") {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    )
}