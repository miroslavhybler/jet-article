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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import jet.html.article.example.ui.theme.JetHtmlArticleExampleTheme
import mir.oslav.jet.html.data.HtmlConfig
import mir.oslav.jet.html.composables.screens.JetHtmlArticle
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.parse.HtmlArticleParser

/**
 *
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023xc
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val view = LocalView.current
            requestInsets(view = view)


            SideEffect {
                requestInsets(view = view)
                ViewCompat.setOnApplyWindowInsetsListener(view) { view, insets ->
                    if (view.findFocus() == null) {
                        val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                        view.updatePadding(bottom = bottom)
                    }
                    insets
                }
                ViewCompat.setWindowInsetsAnimationCallback(
                    view, object : WindowInsetsAnimationCompat.Callback(
                        DISPATCH_MODE_STOP
                    ) {
                        override fun onStart(
                            animation: WindowInsetsAnimationCompat,
                            bounds: WindowInsetsAnimationCompat.BoundsCompat
                        ): WindowInsetsAnimationCompat.BoundsCompat {
                            return super.onStart(animation, bounds)
                        }

                        override fun onProgress(
                            insets: WindowInsetsCompat,
                            runningAnimations: MutableList<WindowInsetsAnimationCompat>,
                        ): WindowInsetsCompat {
                            val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                            view.updatePadding(bottom = bottom)
                            return insets
                        }
                    }
                )

            }

            JetHtmlArticleExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(route = "home") { HomePage(navHostController = navController) }
                        composable(route = "default") { ArticleScreen(article = "default") }
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
) {

    val assets = LocalContext.current.assets

    fun getArticle(fileName: String): String {
        return String(assets.open("simple-examples/${fileName}.html").readBytes())
    }

    val config = remember { HtmlConfig(spanCount = 3) }
    val parseFlow = remember {
        HtmlArticleParser.parse(
            content = getArticle(fileName = article),
            config = config,
        )
    }
    val data: HtmlData by parseFlow.collectAsState(initial = HtmlData.Empty)


    Scaffold { paddingValues ->
        JetHtmlArticle(
            data = data,
            config = config,
            modifier = Modifier,
            contentPadding = paddingValues
        )
    }

}