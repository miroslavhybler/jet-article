package jet.html.article.example.content

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import jet.html.article.example.composables.SimpleTopaBar
import jet.html.article.example.composables.HomeCard
import jet.html.article.example.composables.spacedCard
import jet.html.article.example.data.ExcludeRule
import jet.html.article.example.JetHtmlArticleExampleTheme
import jet.html.article.example.navigateToArticle


/**
 * @author Miroslav Hýbler <br>
 * created on 29.01.2024
 */
@Composable
fun ArticlesScreen(
    navHostController: NavHostController,
) {

    Scaffold(
        topBar = {
            SimpleTopaBar(text = "Articles")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(paddingValues = paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeCard(
                title = "Temporary test",
                description = "Temporary content for debbuging problematic html codes",
                onClick = {
                    navHostController.navigateToArticle(
                        name = "test",
                        excludeRules = listOf(
                            ExcludeRule(keyword = "menu"),
                            )
                    )
                },
                modifier = Modifier.spacedCard()
            )
            HomeCard(
                title = "TEST 2",
                description = "Temporary opening problematic content",
                onClick = {
                    navHostController.navigateToArticle(name = "zing")
                },
                modifier = Modifier.spacedCard()
            )
            HomeCard(
                title = "Finex",
                description = "Finex web article",
                onClick = {
                    navHostController.navigateToArticle(name = "finex")
                },
                modifier = Modifier.spacedCard()
            )

            HomeCard(
                title = "ASCOD",
                description = "Armadni noviny",
                onClick = {
                    navHostController.navigateToArticle(name = "ascod")
                },
                modifier = Modifier.spacedCard()
            )
            HomeCard(
                title = "Mapbox docs",
                description = "Some boring mapbox docs, it's here just because complexity of it's html code",
                onClick = {
                    navHostController.navigateToArticle(name = "mapbox")
                },
                modifier = Modifier.spacedCard()
            )

            HomeCard(
                title = "Android docs",
                description = "Docs about AI Core features with gemini nano",
                onClick = {
                    navHostController.navigateToArticle(
                        name = "android",
                        excludeRules = listOf(
                            ExcludeRule(tag = "devsite-header"),
                            ExcludeRule(tag = "devsite-book-nav"),
                            ExcludeRule(tag = "devsite-footer-linkboxes"),
                            ExcludeRule(tag = "devsite-footer-linkboxes"),
                            ExcludeRule(tag = "devsite-footer-promos"),
                            ExcludeRule(tag = "devsite-footer-utility")
                        )
                    )
                },
                modifier = Modifier.spacedCard()
            )

            HomeCard(
                title = "Wikipedia",
                description = "Wikipedia article about android!",
                onClick = {
                    navHostController.navigateToArticle(name = "wikipedia")
                },
                modifier = Modifier.spacedCard()
            )

            HomeCard(
                title = "Medium",
                description = "Article from medium.com about c++ string performance tips",
                onClick = {
                    navHostController.navigateToArticle(name = "medium")
                },
                modifier = Modifier.spacedCard()
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
private fun ArticlesScrenPreview() {
    JetHtmlArticleExampleTheme {
        ArticlesScreen(navHostController = rememberNavController())
    }
}