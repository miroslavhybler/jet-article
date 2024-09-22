@file:Suppress("ConstPropertyName")

package com.jet.article.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


/**
 * @author Miroslav Hýbler <br>
 * created on 14.09.2024
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navHostController = rememberNavController()
            MaterialTheme(
                colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
            ) {
                NavHost(
                    navController = navHostController,
                    startDestination = Routes.home,
                ) {
                    composable(route = Routes.home) { HomeScreen(navHostController = navHostController) }
                    composable(
                        route = Routes.catalog,
                        arguments = listOf(
                            navArgument(name = "asset") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            },
                            navArgument(name = "title") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            }
                        )
                    ) { backStackEntry ->
                        val asset = backStackEntry.arguments?.getString("asset")
                            ?: throw NullPointerException()
                        val title = backStackEntry.arguments?.getString("title")
                            ?: throw NullPointerException()
                        CatalogScreen(
                            navHostController=navHostController,
                            asset = asset,
                            title = title,
                        )
                    }

                }
            }
        }
    }
}