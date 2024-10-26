package jet.html.article.example.content.test

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jet.article.ui.JetHtmlArticle
import com.jet.article.ui.rememberJetHtmlArticleState
import jet.html.article.example.composables.DebugBottomBar
import jet.html.article.example.composables.Results
import jet.html.article.example.composables.SimpleTopaBar
import jet.html.article.example.highlightstring.HighlightStringActivity


/**
 * @author Miroslav Hýbler <br>
 * created on 30.01.2024
 */
@Composable
fun PerformanceScreen(
    article: String,
    viewModel: BenchmarkViewModel,
    navHostController: NavHostController,
) {
    val context = LocalContext.current
    val testResults by viewModel.testResults.collectAsState()
    val state = rememberJetHtmlArticleState()

    LaunchedEffect(key1 = Unit, block = {
        state.show(
            data = viewModel.loadArticleFromResources(
                article = article,
            )
        )
    })

    BackHandler(enabled = testResults != null) {
        viewModel.testResults.value = null
    }


    Scaffold(
        topBar = {
            SimpleTopaBar(text = "Benchmark: ${state.data.headData.title}")
        },
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                JetHtmlArticle(
                    modifier = Modifier,
                    contentPadding = paddingValues,
                    state = state,
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
                onSearchByIndex = {
                    HighlightStringActivity.launch(
                        context = context,
                        fileName = viewModel.articlePath
                    )
                }
            )
        }
    )
}