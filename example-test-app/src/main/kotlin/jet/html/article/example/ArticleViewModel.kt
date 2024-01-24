package jet.html.article.example

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.jet.article.data.HtmlData
import com.jet.article.ArticleParser
import com.jet.article.ProcessorNative
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

    private val mArticleData: MutableStateFlow<HtmlData> = MutableStateFlow(value = HtmlData.empty)
    val articleData: StateFlow<HtmlData> get() = mArticleData

    fun parse(article: String, ignoreRules: List<Pair<String, String>>) {
        viewModelScope.launch {

            ignoreRules.forEach {
                ProcessorNative.addRule(it.first, it.second)
            }

            val startNano = System.nanoTime()
            val start = System.currentTimeMillis()
            mArticleData.value = ArticleParser.parse(
                content = getArticle(fileName = article),
            )
            val endNano = System.nanoTime()
            val end = System.currentTimeMillis()
            Log.d("mirek", "duration: $ ${end - start}")
            Log.d("mirek", "nano: $ ${endNano - startNano}")

        }
    }


    private fun getArticle(fileName: String): String {
        return String(assets.open("${fileName}.html").readBytes())
    }

}