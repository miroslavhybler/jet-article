package jet.html.article.example.content

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import jet.html.article.example.composables.SimpleTopaBar
import jet.html.article.example.JetHtmlArticleExampleTheme
import jet.html.article.example.composables.HomeCard
import jet.html.article.example.composables.spacedCard
import jet.html.article.example.composables.OpenSource


/**
 * @author Miroslav Hýbler <br>
 * created on 29.01.2024
 */
@Composable
fun HomeScreen(navHostController: NavHostController) {
    Scaffold(
        topBar = {
            SimpleTopaBar()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(paddingValues = paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            Text(
                text = "Testing and benchmarking application for Jet-Article library",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp)
            )

            HomeCard(
                title = "Benchmark",
                description = "Section contains articles that are used for benchmarking and testing. " +
                        "Those are not real and may be adjusted to specific test scenario",
                onClick = {
                    navHostController.navigate(route = "benchmarks")
                },
                modifier = Modifier.spacedCard()
            )

            HomeCard(
                title = "Articles",
                description = "Problematic real articles used for the development to increase relaibility of the library",
                onClick = { navHostController.navigate(route = "articles") },
                modifier = Modifier.spacedCard()
            )

            Spacer(
                modifier = Modifier
                    .defaultMinSize(minHeight = 32.dp)
                    .weight(weight = 1f)
            )

            OpenSource()

            Spacer(modifier = Modifier.height(height = 16.dp))
        }
    }
}


@Composable
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = true,
)
private fun HomePagePreview() {
    JetHtmlArticleExampleTheme {
        HomeScreen(navHostController = rememberNavController())
    }
}


@Composable
@Preview(
    showSystemUi = true,
)
private fun HomePagePreviewLightMode() {
    JetHtmlArticleExampleTheme {
        HomeScreen(navHostController = rememberNavController())
    }
}