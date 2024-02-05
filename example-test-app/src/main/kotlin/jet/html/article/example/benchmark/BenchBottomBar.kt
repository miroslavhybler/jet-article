package jet.html.article.example.benchmark

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
fun BenchBottomBar(
    modifier: Modifier = Modifier,
    onTest: () -> Unit
) {

    BottomAppBar(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxWidth()
    ) {
        Button(onClick = onTest) {
            Text(text = "Run Test")
        }
    }
}