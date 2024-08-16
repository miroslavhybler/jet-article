@file:OptIn(ExperimentalMaterial3Api::class)

package jet.html.article.example.analyzer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jet.article.ArticleAnalyzer
import com.jet.article.data.HtmlAnalyzerData
import com.jet.article.data.HtmlContentType
import com.jet.article.data.TagInfo


/**
 * @author Miroslav Hýbler <br>
 * created on 21.02.2024
 */
@Composable
fun AnalyzerScreen(
    viewModel: AnalyzerViewModel,
    navHostController: NavHostController,
    articleFilePath: String,
) {

    val analyzerData by viewModel.analyzerFlow.collectAsState()

    LaunchedEffect(key1 = Unit, block = {
        viewModel.loadArticleFromResources(article = articleFilePath)
    })

    DisposableEffect(key1 = Unit, effect = {
        onDispose {
            ArticleAnalyzer.clearAllResources()
        }
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Analyzer")
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState())
                    .padding(paddingValues = paddingValues)
            ) {

                AnalyzerDetailsInfo(analyzerData = analyzerData)
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if ((analyzerData as? HtmlAnalyzerData.ContentTag)?.tag?.isPairTag == true) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = viewModel::moveInside) {
                            Text(text = "See tag content")
                        }
                    }
                }

                Button(onClick = viewModel::jumpToBody) {
                    Text(text = "Jump to body")
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Button(onClick = viewModel::moveToPrevious) {
                        Text(text = "Previous step")
                    }

                    Button(onClick = viewModel::doNextStep) {
                        Text(text = "Next step")
                    }
                }
            }
        }
    )
}


@Composable
private fun AnalyzerDetailsInfo(
    modifier: Modifier = Modifier,
    analyzerData: HtmlAnalyzerData,
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {

        when (analyzerData) {
            is HtmlAnalyzerData.ContentTag -> {

                Text(text = "Global", style = MaterialTheme.typography.titleSmall)


                val tag = analyzerData.tag
                DataRow(label = "Type:", value = HtmlContentType.toString(value = tag.contentType))
                DataRow(label = "Tag:", value = tag.tag)
                DataRow(label = "Id:", value = tag.id)
                DataRow(label = "Class:", value = tag.clazz)
                DataRow(label = "Range:", value = "${analyzerData.range}")
                DataRow(label = "Pair:", value = "${analyzerData.tag is TagInfo.Pair}")

                Spacer(modifier = Modifier.height(height = 12.dp))
                Text(text = "Attributes", style = MaterialTheme.typography.titleSmall)

                tag.tagAttributes.forEach { (t, u) ->
                    DataRow(label = t, value = u, labelWeight = 1f, valueWeight = 1f)
                }

                if (analyzerData.tag is TagInfo.Pair) {
                    Text(text = "Pair tag content", style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = (analyzerData.tag as TagInfo.Pair).content,
                        style = MaterialTheme.typography.bodySmall
                    )
                }


            }

            is HtmlAnalyzerData.ParseError -> {
                Text(
                    text = "ERROR",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error
                )

                DataRow(label = "Cause:", value = "${analyzerData.cause}")
                DataRow(label = "Message:", value = analyzerData.errorMessage)
                DataRow(label = "Range:", value = "${analyzerData.range}")

            }

            is HtmlAnalyzerData.Empty -> {
                Text(text = "EMPTY")
            }
        }

    }
}


@Composable
private fun DataRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String?,
    labelWeight: Float = 0.3f,
    valueWeight: Float = 0.7f,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {

        Text(
            text = label.trim(),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.weight(weight = labelWeight),
            color = color
        )

        Spacer(modifier = Modifier.width(width = 12.dp))

        Text(
            text = value?.trim() ?: "---",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(weight = valueWeight),
            color = color
        )
    }
}


