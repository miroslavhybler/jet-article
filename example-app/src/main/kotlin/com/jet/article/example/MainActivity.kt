@file:OptIn(JetExperimental::class)

package com.jet.article.example

import android.os.Bundle
import android.webkit.URLUtil
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jet.article.ArticleParser
import com.jet.article.data.HtmlData
import com.jet.article.example.ui.theme.JetHtmlArticleTheme
import com.jet.article.ui.JetHtmlArticle
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import kotlinx.coroutines.launch
import mir.oslav.jet.annotations.JetExperimental

/**
 * @since 1.0.0
 * @author Miroslav Hýbler <br>
 * created on 22.01.2024
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var url by remember { mutableStateOf(value = "") }
            var htmlData: HtmlData by remember { mutableStateOf(value = HtmlData.empty) }
            val coroutineScope = rememberCoroutineScope()

            BackHandler(enabled = htmlData != HtmlData.empty) {
                htmlData = HtmlData.empty
            }

            JetHtmlArticleTheme {
                Surface {
                    if (htmlData != HtmlData.empty) {
                        JetHtmlArticle(data = htmlData)
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            OutlinedTextField(
                                value = url,
                                onValueChange = { url = it },
                                modifier = Modifier
                                    .padding(horizontal = 20.dp)
                                    .fillMaxWidth()
                            )

                            Button(onClick = {
                                if (URLUtil.isValidUrl(url)) {
                                    coroutineScope.launch {
                                        htmlData = ArticleParser.parse(
                                            content = loadFromUrl(url = url)
                                        )
                                    }
                                }
                            }) {
                                Text(text = "Load article")
                            }
                        }
                    }
                }
            }
        }
    }


    private suspend fun loadFromUrl(url: String): String {
        val response: HttpResponse = ktorHttpClient.get(urlString = url)
        return response.readText()
    }
}