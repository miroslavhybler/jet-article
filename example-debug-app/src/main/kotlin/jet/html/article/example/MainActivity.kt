@file:OptIn(ExperimentalMaterial3Api::class)

package jet.html.article.example

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import jet.html.article.example.content.ArticlesScreen
import jet.html.article.example.content.BenchmarksScreen
import jet.html.article.example.data.ExcludeRule
import jet.html.article.example.content.article.ArticleScreen
import jet.html.article.example.content.HomeScreen
import jet.html.article.example.analyzer.AnalyzerScreen
import jet.html.article.example.content.benchmark.BenchmarkScreen

/**
 *
 * @author Miroslav Hýbler <br>
 * created on 25.08.2023
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
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
                    val systemUiController = rememberSystemUiController()
                    val isAppDark = isSystemInDarkTheme()
                    val colorScheme = MaterialTheme.colorScheme

                    LaunchedEffect(key1 = systemUiController, key2 = isAppDark, block = {
                        systemUiController.setSystemBarsColor(
                            color = colorScheme.background,
                            darkIcons = !isAppDark
                        )
                    })

                    NavHost(
                        navController = navHostController,
                        startDestination = "home",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(route = "home") {
                            HomeScreen(navHostController = navHostController)
                        }
                        composable(
                            route = "articles",
                            enterTransition = { fadeIn() },
                            exitTransition = { fadeOut() }
                        ) {
                            ArticlesScreen(navHostController = navHostController)
                        }
                        composable(
                            route = "benchmarks",
                            enterTransition = { fadeIn() },
                            exitTransition = { fadeOut() }
                        ) {
                            BenchmarksScreen(navHostController = navHostController)
                        }
                        composable(
                            route = "article-screen?article={article}",
                            enterTransition = { slideInHorizontally { it } },
                            exitTransition = { slideOutHorizontally { it } },
                            arguments = listOf(
                                navArgument(name = "article") {
                                    type = NavType.StringType
                                    nullable = false
                                },
                            )
                        ) {
                            val res = it.arguments?.getString("article")
                                ?: throw NullPointerException("")
                            ArticleScreen(
                                article = res,
                                navHostController = navHostController,
                                viewModel = hiltViewModel(),
                            )
                        }
                        composable(
                            route = "benchmark-screen?article={article}",
                            enterTransition = { slideInHorizontally { it } },
                            exitTransition = { slideOutHorizontally { it } },
                            arguments = listOf(
                                navArgument(name = "article") {
                                    type = NavType.StringType
                                    nullable = false
                                },
                            )
                        ) {
                            val res = it.arguments?.getString("article")
                                ?: throw NullPointerException("")
                            BenchmarkScreen(
                                article = res,
                                viewModel = hiltViewModel(),
                                navHostController = navHostController
                            )
                        }

                        composable(
                            route = "analyzer?articlePath={articlePath}",
                            arguments = listOf(
                                navArgument(name = "articlePath") {
                                    type = NavType.StringType
                                    nullable = false
                                },
                            )
                        ) {

                            val articleFilePath = it.arguments?.getString("articlePath")
                                ?: throw NullPointerException("articleFilePath is null")

                            AnalyzerScreen(
                                viewModel = hiltViewModel(),
                                navHostController = navHostController,
                                articleFilePath = articleFilePath
                            )
                        }
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
fun JetHtmlArticleExampleTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        isDarkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}

fun benchmarkRoute(name: String, excludeRules: List<ExcludeRule>): String {
    ExcludeRule.globalRules = excludeRules
    return "benchmark-screen?article=$name"
}

fun articleRoute(name: String, excludeRules: List<ExcludeRule>): String {
    ExcludeRule.globalRules = excludeRules
    return "article-screen?article=$name"
}

fun NavHostController.navigateToArticle(
    name: String,
    excludeRules: List<ExcludeRule> = emptyList()
) {
    ExcludeRule.globalRules = excludeRules
    navigate(route = articleRoute(name = name, excludeRules = excludeRules))
}

fun NavHostController.navigateToBenchmark(
    name: String,
    excludeRules: List<ExcludeRule> = emptyList()
) {
    ExcludeRule.globalRules = excludeRules
    navigate(route = benchmarkRoute(name = name, excludeRules = excludeRules))
}