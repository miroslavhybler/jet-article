@file:OptIn(JetExperimental::class)

package com.jet.article.example.devblog.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.jet.article.ArticleParser
import com.jet.article.example.devblog.rememberSystemBarsStyle
import com.jet.article.example.devblog.ui.main.MainScreen
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
            JetArticleTheme {
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
                    MainScreen(viewModel = hiltViewModel())
                }
            }
        }
    }
}