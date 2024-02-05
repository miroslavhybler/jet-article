package jet.html.article.example.benchmark

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jet.article.ArticleParser
import com.jet.article.ProcessorNative
import com.jet.article.data.HtmlData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jet.html.article.example.data.ExcludeRule
import jet.html.article.example.data.TestResults
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val mArticleData: MutableStateFlow<HtmlData> = MutableStateFlow(value = HtmlData.empty)
    val articleData: StateFlow<HtmlData> get() = mArticleData


    val testResults: MutableStateFlow<TestResults?> = MutableStateFlow(value = null)


    private var article: String = ""

    fun loadArticleFromResources(article: String, ignoreRules: List<ExcludeRule>) {
        this.article = article
        viewModelScope.launch {
            ignoreRules.forEach {
                ProcessorNative.addRule(tag = it.tag, clazz = it.clazz)
            }
            mArticleData.value = ArticleParser.parse(
                content = getArticle(fileName = article),
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

                ArticleParser.parse(content = getArticle(fileName = article))

                val endNano = System.nanoTime()
                val end = System.currentTimeMillis()

                millis.add(end - start)
                nanos.add(endNano - startNano)
            }

            testResults.value = TestResults(durationsMillis = millis, durationsNano = nanos)
        }
    }


    private fun getArticle(fileName: String): String {
        return String(assets.open("benchmark/${fileName}.html").readBytes())
    }

}