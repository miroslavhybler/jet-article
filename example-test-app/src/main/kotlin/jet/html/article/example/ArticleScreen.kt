@file:OptIn(ExperimentalMaterial3Api::class)

package jet.html.article.example

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import mir.oslav.jet.html.article.data.HtmlData
import mir.oslav.jet.html.article.ui.JetHtmlArticle

@Composable
fun ArticleScreen(
    article: String,
    viewModel: ArticleViewModel = hiltViewModel()
) {

    val data: HtmlData by viewModel.articleData.collectAsState()

    LaunchedEffect(key1 = Unit, block = {
        if (data is HtmlData.Empty) {
            viewModel.parse(article = article)
        }
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = remember(key1 = data) {
                        (data as? HtmlData.Success)?.headData?.title ?: "No title"
                    })
                }
            )
        }
    ) { paddingValues ->
        JetHtmlArticle(
            data = data,
            modifier = Modifier,
            contentPadding = paddingValues
        )
    }
}
