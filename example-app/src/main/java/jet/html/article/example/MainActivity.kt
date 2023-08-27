package jet.html.article.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import jet.html.article.example.ui.theme.JetHtmlArticleTheme
import mir.oslav.jet.html.composables.HtmlConfig
import mir.oslav.jet.html.composables.JetHtmlArticle
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.parse.HtmlArticleParser

/**
 * @author Miroslav Hýbler <br>
 * created on 25.0.2023
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetHtmlArticleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {

                        composable(route = "home") {
                            HomePage(navHostController = navController)
                        }

                        composable(route = "article/default") {
                            ArticleScreen(
                                article = "default",
                                navHostController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun HomePage(navHostController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Button(
            onClick = {
                navHostController.navigate(route = "article/default")
            }, content = {
                Text(text = "Default")
            }
        )
    }
}


@Composable
private fun ArticleScreen(
    article: String,
    navHostController: NavHostController
) {

    val assets = LocalContext.current.assets

    fun getArticle(fileName: String): String {
        return String(assets.open("simple-examples/${fileName}.html").readBytes())
    }

    var data: HtmlData? by remember { mutableStateOf(value = null) }

    val config = remember { HtmlConfig(spanCount = 3) }

    LaunchedEffect(key1 = Unit, block = {
        data = HtmlArticleParser.parse(
            content = getArticle(fileName = article),
            config = config
        )
    })

    data?.let {
        JetHtmlArticle(
            data = it,
            config = config,
            navHostController = navHostController
        )
    }
}