@file:OptIn(JetExperimental::class)

package com.jet.article.example.devblog.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jet.article.example.devblog.data.SettingsStorage
import com.jet.article.example.devblog.isAppDark
import com.jet.article.example.devblog.rememberSystemBarsStyle
import com.jet.article.example.devblog.ui.home.MainScreen
import com.jet.article.example.devblog.ui.settings.AboutLibsScreen
import com.jet.article.example.devblog.ui.settings.AboutScreen
import com.jet.article.example.devblog.ui.settings.ChangelogScreen
import com.jet.article.example.devblog.ui.settings.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint
import mir.oslav.jet.annotations.JetExperimental

/**
 * @author Miroslav Hýbler <br>
 * created on 09.08.2024
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    companion object {
        var isActive: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        isActive = true
        setContent {
            val settings by viewModel.settings.collectAsState(
                initial = SettingsStorage.Settings()
            )
            val systemBarsStyle = rememberSystemBarsStyle(settings = settings)
            val dimensions = rememberDimensions()

            DevBlogAppTheme(
                isUsingDynamicColors = settings.isUsingDynamicColors,
                darkTheme = isAppDark(settings = settings),
            ) {
                CompositionLocalProvider(
                    value = LocalDimensions provides dimensions,
                ) {
                    LaunchedEffect(key1 = systemBarsStyle) {
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

                        composable(
                            route = Routes.aboutLibs,
                            enterTransition = { fadeIn() },
                            exitTransition = { fadeOut() },
                        ) {
                            AboutLibsScreen(
                                navHostController = navHostController,
                            )
                        }
                        composable(
                            route = Routes.channelLog,
                            enterTransition = { fadeIn() },
                            exitTransition = { fadeOut() },
                        ) {
                            ChangelogScreen(
                                navHostController = navHostController,
                            )
                        }
                        composable(
                            route = Routes.about,
                            enterTransition = { fadeIn() },
                            exitTransition = { fadeOut() },
                        ) {
                            AboutScreen(
                                navHostController = navHostController,
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        isActive = false
        super.onDestroy()
    }
}