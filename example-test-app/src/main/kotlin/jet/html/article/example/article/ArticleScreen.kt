@file:OptIn(ExperimentalMaterial3Api::class)

package jet.html.article.example.article

import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jet.article.data.HtmlData
import com.jet.article.ui.JetHtmlArticle
import jet.html.article.example.data.ExcludeRule

@Composable
fun ArticleScreen(
    article: String,
    viewModel: ArticleViewModel = hiltViewModel(),
) {

    val data: HtmlData by viewModel.articleData.collectAsState()

    LaunchedEffect(key1 = Unit, block = {
        viewModel.loadArticleFromResources(
            article = article,
            excludeRules = ExcludeRule.globalRules
        )
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = remember(key1 = data) {
                        data.headData.title ?: "No title"
                    })
                }
            )
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


@Composable
fun rememberIgnoreRules(vararg rules: Pair<String, String>): List<Pair<String, String>> {
    return remember {
        rules.toList()
    }
}