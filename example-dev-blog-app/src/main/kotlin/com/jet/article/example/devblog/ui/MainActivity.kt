@file:OptIn(JetExperimental::class)

package com.jet.article.example.devblog.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jet.article.example.devblog.rememberSystemBarsStyle
import com.jet.article.example.devblog.ui.main.MainScreen
import com.jet.article.example.devblog.ui.settings.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint
import mir.oslav.jet.annotations.JetExperimental

/**
 * @author Miroslav Hýbler <br>
 * created on 09.08.2024
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DevBlogAppTheme {
                val systemBarsStyle = rememberSystemBarsStyle()
                CompositionLocalProvider(
                    LocalDimensions provides rememberDimensions()
                ) {
                    LaunchedEffect(key1 = Unit) {
                        enableEdgeToEdge(
                            statusBarStyle = systemBarsStyle,
                            navigationBarStyle = systemBarsStyle,
                        )
                    }
                    val navHostController = rememberNavController()
                    NavHost(
                        navController = navHostController,
                        startDestination = Routes.main,
                    ) {

                        composable(route = Routes.main) {
                            MainScreen(
                                viewModel = hiltViewModel(),
                                navHostController = navHostController,
                            )
                        }

                        composable(
                            route = Routes.settings,
                            enterTransition = { fadeIn() },
                            exitTransition = { fadeOut() },
                        ) {
                            SettingsScreen(
                                navHostController = navHostController,
                                viewModel = hiltViewModel(),
                            )
                        }
                    }
                }
            }
        }
    }
}