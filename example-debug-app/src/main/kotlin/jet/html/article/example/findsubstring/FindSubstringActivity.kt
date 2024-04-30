package jet.html.article.example.findsubstring

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dagger.hilt.android.AndroidEntryPoint
import jet.html.article.example.JetHtmlArticleExampleTheme


/**
 * @author Miroslav Hýbler <br>
 * created on 30.04.2024
 */
@AndroidEntryPoint
class FindSubstringActivity : ComponentActivity() {


    private val viewModel: FindSubstringViewModel by viewModels()

    companion object {

        fun launch(context: Context, fileName: String) {
            context.launchFindSubstringActivity(fileName = fileName)
        }

        fun Context.launchFindSubstringActivity(fileName: String) {
            startActivity(
                Intent(this, FindSubstringActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("fileName", fileName)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fileName = intent.getStringExtra("fileName")
            ?: throw NullPointerException("fileName not provided")
        viewModel.loadArticleFromResources(fileName)

        setContent {
            JetHtmlArticleExampleTheme {

                val articleContent by viewModel.articleData.collectAsState()
                val colorScheme = MaterialTheme.colorScheme
                var text by remember { mutableStateOf(value = "") }
                val index = remember(key1 = text) { text.toIntOrNull() ?: 0 }
                var bounds: Rect? by remember { mutableStateOf(value = null) }
                val scrollState = rememberScrollState()

                LaunchedEffect(key1 = bounds) {
                    bounds?.let {
                        scrollState.scrollTo(value = it.bottom.toInt())
                    }
                }

                Scaffold(
                    topBar = {
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            modifier = Modifier
                                .statusBarsPadding()
                                .fillMaxWidth()
                                .background(color = MaterialTheme.colorScheme.background),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Number,
                            ),
                        )
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state = scrollState)
                            .padding(paddingValues = paddingValues)
                    ) {
                        Text(
                            text = remember(key1 = text) {
                                buildAnnotatedString {
                                    if (text.isEmpty()) {
                                        append(articleContent)
                                        return@buildAnnotatedString
                                    }


                                    append(
                                        articleContent.substring(
                                            startIndex = 0,
                                            endIndex = (index - 10).coerceAtLeast(minimumValue = 0),
                                        )
                                    )

                                    withStyle(
                                        SpanStyle(
                                            background = colorScheme.primaryContainer,
                                            color = colorScheme.onPrimaryContainer,
                                        )
                                    ) {
                                        append(
                                            articleContent.substring(
                                                startIndex = index - 10,
                                                endIndex = (index + 10)
                                                    .coerceAtMost(maximumValue = articleContent.length)
                                            ),
                                        )
                                    }

                                    append(
                                        articleContent.substring(
                                            startIndex = (index + 10)
                                                .coerceAtMost(maximumValue = articleContent.length),
                                            endIndex = articleContent.length,
                                        )
                                    )
                                }
                            },
                            onTextLayout = { textLayoutResult ->
                                bounds = textLayoutResult.getBoundingBox(offset = index)
                            },
                            fontSize = 11.sp,
                            modifier = Modifier.padding(
                                top = 16.dp,
                                bottom = 16.dp,
                                start = 4.dp,
                                end = 4.dp,
                            )
                        )
                    }
                }
            }
        }
    }
}