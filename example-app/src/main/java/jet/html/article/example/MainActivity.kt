package jet.html.article.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import jet.html.article.example.ui.theme.JetHtmlArticleTheme
import mir.oslav.jet.html.composables.JetHtml
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.parse.HtmlParser

/**
 * @author Miroslav Hýbler <br>
 * created on 25.0.2023
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetHtmlArticleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    var data: HtmlData? by remember { mutableStateOf(value = null) }

                    LaunchedEffect(key1 = Unit, block = {
                        data = HtmlParser.parse(
                            content = getArticle(fileName = "default"),
                        )
                    })

                    data?.let {
                        JetHtml(data = it)
                    }
                }
            }
        }
    }


    private fun getArticle(fileName: String): String {
        return String(assets.open("simple-examples/${fileName}.html").readBytes())
    }
}