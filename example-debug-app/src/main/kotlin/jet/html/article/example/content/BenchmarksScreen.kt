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
import jet.html.article.example.composables.HomeCard
import jet.html.article.example.composables.SimpleTopaBar
import jet.html.article.example.composables.spacedCard
import jet.html.article.example.data.ExcludeRule
import jet.html.article.example.navigateToBenchmark


/**
 * @author Miroslav Hýbler <br>
 * created on 30.01.2024
 */
@Composable
fun BenchmarksScreen(
    navHostController: NavHostController
) {

    Scaffold(
        topBar = {
            SimpleTopaBar(text = "Benchmarks")
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
                title = "Exclude rules",
                description = "Testing excuding some content, simple test of exclude rules",
                onClick = {
                    navHostController.navigateToBenchmark(
                        name = "ignore-test",
                        excludeRules = listOf(
                            ExcludeRule(tag = "div", clazz = "menu"),
                            ExcludeRule(tag = "div", clazz = "resources"),
                            ExcludeRule(keyword = "header"),
                            ExcludeRule(keyword = "footer"),
                            ExcludeRule(keyword = "menu"),
                            ExcludeRule(keyword = "lang"),
                            ExcludeRule(keyword = "date"),
                            ExcludeRule(keyword = "weather"),
                            ExcludeRule(keyword = "search"),
                            ExcludeRule(keyword = "bar"),
                            ExcludeRule(keyword = "resources"),
                            ExcludeRule(keyword = "social"),
                            ExcludeRule(keyword = "weather"),
                            ExcludeRule(keyword = "tags"),
                            ExcludeRule(keyword = "megamenu")
                        )
                    )
                },
                modifier = Modifier.spacedCard()
            )
            HomeCard(
                title = "Text unsupported",
                description = "Clearing unsupported tags from text blocks",
                onClick = {
                    navHostController.navigateToBenchmark(
                        name = "text-unsupported",
                    )
                },
                modifier = Modifier.spacedCard()
            )
            HomeCard(
                title = "Links",
                description = "Testing links",
                onClick = {
                    navHostController.navigateToBenchmark(
                        name = "links-test",
                    )
                },
                modifier = Modifier.spacedCard()
            )

            HomeCard(
                title = "Elements test",
                description = "Simple preview of supported attributes",
                onClick = {
                    navHostController.navigateToBenchmark(name = "elements-test")
                },
                modifier = Modifier.spacedCard()
            )
            HomeCard(
                title = "Simple file",
                description = "",
                onClick = {
                    navHostController.navigateToBenchmark(name = "simple")
                },
                modifier = Modifier.spacedCard()
            )

        }
    }
}


@Composable
@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
private fun BenchmarksScreenPreview() {
    BenchmarksScreen(navHostController = rememberNavController())
}