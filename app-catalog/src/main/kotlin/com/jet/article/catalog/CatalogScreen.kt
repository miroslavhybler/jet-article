@file:OptIn(JetExperimental::class, ExperimentalMaterial3Api::class)

package com.jet.article.catalog

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jet.article.ArticleParser
import com.jet.article.data.HtmlArticleData
import com.jet.article.ui.JetHtmlArticle
import com.jet.article.ui.rememberJetHtmlArticleState
import mir.oslav.jet.annotations.JetExperimental


/**
 * Default Catalog screen
 * @author Miroslav Hýbler <br>
 * created on 20.09.2024
 */
@Composable
fun CatalogScreen(
    navHostController: NavHostController,
    title: String,
    asset: String
) {
    val context = LocalContext.current
    val layoutDirection = LocalLayoutDirection.current
    val state = rememberJetHtmlArticleState()

    LaunchedEffect(key1 = asset) {
        state.show(
            data = ArticleParser.parse(
                content = context.getHtmlAsset(fileName = asset),
                url = "https://www.example.com",
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = title)
                },
                navigationIcon = {
                    IconButton(
                        onClick = navHostController::popBackStack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            JetHtmlArticle(
                modifier = Modifier,
                state = state,
                contentPadding = paddingValues,
            )
        },
        bottomBar = {

        }
    )
}