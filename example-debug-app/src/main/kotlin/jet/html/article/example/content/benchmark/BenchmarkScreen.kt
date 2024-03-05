package jet.html.article.example.content.benchmark

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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.jet.article.ui.JetHtmlArticle
import jet.html.article.example.composables.DebugBottomBar
import jet.html.article.example.composables.Results
import jet.html.article.example.data.ExcludeRule
import jet.html.article.example.composables.SimpleTopaBar


/**
 * @author Miroslav Hýbler <br>
 * created on 30.01.2024
 */
@Composable
fun BenchmarkScreen(
    article: String,
    viewModel: BenchmarkViewModel,
    navHostController: NavHostController,
) {

    val data by viewModel.articleData.collectAsState()
    val testResults by viewModel.testResults.collectAsState()

    LaunchedEffect(key1 = Unit, block = {
        viewModel.loadArticleFromResources(
            article = article,
            ignoreRules = ExcludeRule.globalRules
        )
    })

    BackHandler(enabled = testResults != null) {
        viewModel.testResults.value = null
    }


    Scaffold(
        topBar = {
            SimpleTopaBar(text = "Benchmark: ${data.headData.title}")
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
                }
            )
        }
    )
}