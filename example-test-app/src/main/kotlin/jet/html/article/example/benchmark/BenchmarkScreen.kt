package jet.html.article.example.benchmark

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.jet.article.ui.JetHtmlArticle
import jet.html.article.example.composables.SimpleTopaBar


/**
 * @author Miroslav Hýbler <br>
 * created on 30.01.2024
 */
@Composable
fun BenchmarkScreen(
    article: String,
    viewModel: BenchmarkViewModel = hiltViewModel()
) {

    val data by viewModel.articleData.collectAsState()

    LaunchedEffect(key1 = Unit, block = {
        viewModel.loadArticleFromResources(article = article, ignoreRules = emptyList())
    })


    Scaffold(
        topBar = {
            SimpleTopaBar(text = "Benchmark")
        }
    ) { paddingValues ->
        JetHtmlArticle(
            data = data,
            modifier = Modifier,
            contentPadding = remember(key1 = paddingValues) {
                PaddingValues(
                    top = paddingValues.calculateTopPadding() + 32.dp,
                    bottom = paddingValues.calculateBottomPadding() + 16.dp,
                    start = 18.dp,
                    end = 18.dp
                )
            }
        )
    }
}