package jet.html.article.example.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


/**
 * @author Miroslav Hýbler <br>
 * created on 05.02.2024
 */
@Composable
fun DebugBottomBar(
    modifier: Modifier = Modifier,
    onTest: () -> Unit,
    onSearchByIndex: () -> Unit,
) {

    BottomAppBar(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxWidth()
    ) {
        Button(onClick = onTest) {
            Text(text = "Run Test")
        }

        Button(onClick = onSearchByIndex) {
            Text(text = "Search by index")
        }
    }
}