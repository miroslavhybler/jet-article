package jet.html.article.example.highlightstring

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import dagger.hilt.android.AndroidEntryPoint
import jet.html.article.example.JetHtmlArticleExampleTheme


/**
 * @author Miroslav Hýbler <br>
 * created on 30.04.2024
 */
@AndroidEntryPoint
class HighlightStringActivity : ComponentActivity() {


    private val viewModel: HighlightStringViewModel by viewModels()

    companion object {

        fun launch(context: Context, fileName: String) {
            context.startActivity(
                Intent(context, HighlightStringActivity::class.java)
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
                val imeController = LocalSoftwareKeyboardController.current
                val articleContent by viewModel.articleData.collectAsState()
                var indexText by remember { mutableStateOf(value = "") }
                var index by remember { mutableIntStateOf(value = indexText.toIntOrNull() ?: 0) }
                val scrollState = rememberScrollState()

                Scaffold(
                    topBar = {
                        OutlinedTextField(
                            value = indexText,
                            onValueChange = {
                                indexText = it
                                index = it.toIntOrNull() ?: 0
                            },
                            modifier = Modifier
                                .statusBarsPadding()
                                .background(color = MaterialTheme.colorScheme.background),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Number,
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    index = index
                                    imeController?.hide()
                                }
                            )
                        )
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state = scrollState)
                            .padding(paddingValues = paddingValues)
                    ) {
                        HightlightedText(
                            text = articleContent,
                            scrollState = scrollState,
                            index = index
                        )
                    }
                }
            }
        }
    }
}