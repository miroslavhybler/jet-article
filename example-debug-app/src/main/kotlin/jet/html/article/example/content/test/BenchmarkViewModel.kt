package jet.html.article.example.content.test

import android.content.Context
import android.content.res.AssetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jet.article.ArticleParser
import com.jet.article.data.HtmlArticleData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jet.html.article.example.data.ExcludeRule
import jet.html.article.example.data.TestResults
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * @author Miroslav Hýbler <br>
 * created on 30.01.2024
 */
@HiltViewModel
class BenchmarkViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val assets: AssetManager = context.assets

    val testResults: MutableStateFlow<TestResults?> = MutableStateFlow(value = null)

    var articlePath: String = ""
    private var article: String = ""

    suspend fun loadArticleFromResources(
        article: String
    ): HtmlArticleData {
        this.article = article
        ExcludeRule.globalRules.forEach {
            ArticleParser.addExcludeOption(
                tag = it.tag,
                clazz = it.clazz,
                id = it.id,
                keyword = it.keyword,
            )
        }
        val articleContent = getArticle(fileName = article)

        ArticleParser.initialize(
            isQueringTextOutsideTextTags = ExcludeRule.isQuearingTextOutsideTags,
            isTextFormattingEnabled = ExcludeRule.isTextFormattingEnabled,
            areImagesEnabled = true,
            isLoggingEnabled = true,
        )

        return ArticleParser.parse(
            content = articleContent,
            url = "https://www.example.com"
        )
    }


    fun runTest() {
        viewModelScope.launch {
            val millis: ArrayList<Long> = ArrayList()
            val nanos: ArrayList<Long> = ArrayList()
            for (i in 0 until 10) {
                val startNano = System.nanoTime()
                val start = System.currentTimeMillis()

                ArticleParser.parse(
                    content = getArticle(fileName = article),
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
        articlePath = "test/${fileName}.html"
        return String(assets.open(articlePath).readBytes())
    }

}