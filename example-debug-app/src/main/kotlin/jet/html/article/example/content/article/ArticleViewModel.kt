package jet.html.article.example.content.article

import android.content.Context
import android.content.res.AssetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.jet.article.data.HtmlArticleData
import com.jet.article.ArticleParser
import jet.html.article.example.data.ExcludeRule
import jet.html.article.example.data.TestResults
import javax.inject.Inject


/**
 * @author Miroslav Hýbler <br>
 * created on 13.12.2023
 */
@HiltViewModel
class ArticleViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val assets: AssetManager = context.assets

    private val mArticleData: MutableStateFlow<HtmlArticleData> =
        MutableStateFlow(value = HtmlArticleData.empty)
    val articleData: StateFlow<HtmlArticleData> get() = mArticleData


    val testResults: MutableStateFlow<TestResults?> = MutableStateFlow(value = null)


    var articlePath: String = ""
        private set

    fun loadArticleFromResources(
        article: String,
    ) {
        viewModelScope.launch {
            ExcludeRule.globalRules.forEach { excludeRule ->
                ArticleParser.addExcludeOption(
                    keyword = excludeRule.keyword,
                    tag = excludeRule.tag,
                    id = excludeRule.id,
                    clazz = excludeRule.clazz
                )
            }

            mArticleData.value = ArticleParser.parse(
                content = getArticle(fileName = article),
                url = getUrlForArticle(fileName = article)
            )
        }
    }


    fun runTest() {
        viewModelScope.launch {
            val millis: ArrayList<Long> = ArrayList()
            val nanos: ArrayList<Long> = ArrayList()
            for (i in 0 until 10) {
                val startNano = System.nanoTime()
                val start = System.currentTimeMillis()

                ArticleParser.parse(
                    content = articlePath,
                    url = "https://www.example.com"
                )

                val endNano = System.nanoTime()
                val end = System.currentTimeMillis()

                millis.add(end - start)
                nanos.add(endNano - startNano)
            }

            testResults.value = TestResults(durationsMillis = millis, durationsNano = nanos)
        }
    }

    private fun getArticle(fileName: String): String {
        articlePath = "articles/${fileName}.html"
        return String(assets.open(articlePath).readBytes())
    }


    private fun getUrlForArticle(fileName: String): String {
        return when (fileName) {
            "ascod" -> "https://armadnizpravodaj.cz/pozemni-technika/ascod-acr/"
            else -> ""
        }
    }
}