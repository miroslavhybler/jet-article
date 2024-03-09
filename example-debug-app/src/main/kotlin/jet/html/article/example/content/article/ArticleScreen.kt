@file:OptIn(ExperimentalMaterial3Api::class)

package jet.html.article.example.content.article

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jet.article.data.HtmlArticleData
import com.jet.article.ui.JetHtmlArticle
import jet.html.article.example.composables.DebugBottomBar
import jet.html.article.example.composables.Results
import jet.html.article.example.composables.SimpleTopaBar
import jet.html.article.example.data.ExcludeRule

@Composable
fun ArticleScreen(
    article: String,
    viewModel: ArticleViewModel,
    navHostController: NavHostController,
) {

    val data: HtmlArticleData by viewModel.articleData.collectAsState()

    val testResults by viewModel.testResults.collectAsState()

    BackHandler(enabled = testResults != null) {
        viewModel.testResults.value = null
    }

    LaunchedEffect(key1 = Unit, block = {
        viewModel.loadArticleFromResources(
            article = article,
            excludeRules = ExcludeRule.globalRules
        )
    })

    Scaffold(
        topBar = {
            SimpleTopaBar(text = data.headData.title ?: "---")
        },
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
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


                AnimatedVisibility(
                    visible = testResults != null,
                    modifier = Modifier.align(alignment = Alignment.BottomCenter),
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it }
                ) {
                    testResults?.let {
                        Results(
                            results = it,
                            modifier = Modifier
                                .padding(paddingValues = paddingValues)
                                .fillMaxWidth()
                                .height(height = 256.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            DebugBottomBar(
                onTest = viewModel::runTest,
                onAnalyzer = {
                    navHostController.navigate(route = "analyzer?articlePath=${viewModel.articlePath}")
                },
            )
        }
    )
}


@Composable
fun rememberIgnoreRules(vararg rules: Pair<String, String>): List<Pair<String, String>> {
    return remember {
        rules.toList()
    }
}