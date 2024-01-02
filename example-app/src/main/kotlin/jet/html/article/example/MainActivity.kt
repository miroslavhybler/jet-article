@file:OptIn(ExperimentalMaterial3Api::class)

package jet.html.article.example

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import jet.html.article.example.ui.theme.JetHtmlArticleExampleTheme
import mir.oslav.jet.html.data.HtmlConfig
import mir.oslav.jet.html.ui.JetHtmlArticle
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.IgnoreOptions

/**
 *
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val colorScheme = MaterialTheme.colorScheme
            val view = LocalView.current


            JetHtmlArticleExampleTheme {
                LaunchedEffect(key1 = Unit, block = {
                    requestInsets(view = view)
                })


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navHostController = rememberNavController()

                    NavHost(
                        navController = navHostController,
                        startDestination = "home",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(route = "home") { HomePage(navHostController = navHostController) }
                        composable(route = "default") { ArticleScreen(article = "default") }
                        composable(route = "simple") { ArticleScreen(article = "simple") }
                        composable(route = "mapbox") { ArticleScreen(article = "mapbox") }
                        composable(route = "android") { ArticleScreen(article = "android") }
                    }
                }
            }
        }
    }


    private fun requestInsets(view: View) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, view)
            .show(WindowInsetsCompat.Type.systemBars())
    }
}


@Composable
private fun HomePage(navHostController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.weight(weight = 1f))

        Button(
            onClick = {
                navHostController.navigate(route = "default")
            }, content = {
                Text(text = "Default")
            }
        )
        Button(
            onClick = {
                navHostController.navigate(route = "simple")
            }, content = {
                Text(text = "Simple")
            }
        )
        Button(
            onClick = {
                navHostController.navigate(route = "mapbox")
            }, content = {
                Text(text = "Mapbox docs")
            }
        )
        Button(
            onClick = {
                navHostController.navigate(route = "android")
            }, content = {
                Text(text = "Android docs")
            }
        )
        Spacer(modifier = Modifier.weight(weight = 1f))


        Text(
            text = buildString {
                append(BuildConfig.VERSION_NAME)
                append(" (build ${BuildConfig.VERSION_CODE})")
            },
            modifier = Modifier
                .navigationBarsPadding()
                .wrapContentSize()

        )
    }
}


@Composable
private fun ArticleScreen(
    article: String,
    ignoreOptions: IgnoreOptions = IgnoreOptions(),
    viewModel: ArticleViewModel = hiltViewModel()
) {

    val config = remember { HtmlConfig(spanCount = 3) }
    val data: HtmlData by viewModel.articleData.collectAsState()

    LaunchedEffect(key1 = Unit, block = {
       if (data.isEmpty) {
            viewModel.parse(
                config = config,
                ignoreOptions = ignoreOptions,
                article = article
            )
        }
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = remember(key1 = data) {
                        data.headData?.title ?: "No title"
                    })
                }
            )
        }
    ) { paddingValues ->
        JetHtmlArticle(
            data = data,
            config = config,
            modifier = Modifier,
            contentPadding = paddingValues
        )
    }
}