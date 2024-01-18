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
import mir.oslav.jet.html.article.data.HtmlData
import mir.oslav.jet.html.article.JetHtmlArticleParser
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

    private val mArticleData: MutableStateFlow<HtmlData> = MutableStateFlow(value = HtmlData.Empty)
    val articleData: StateFlow<HtmlData> get() = mArticleData

    fun parse(article: String) {
        viewModelScope.launch {
            val start = System.currentTimeMillis()
            Log.d("mirek", "start: $start")
            mArticleData.value = JetHtmlArticleParser.parse(
                content = getArticle(fileName = article),
            )
            val end = System.currentTimeMillis()
            Log.d("mirek", "end: $end")
            Log.d("mirek", "duration: ${end - start}")
        }
    }


    private fun getArticle(fileName: String): String {
        return String(assets.open("${fileName}.html").readBytes())
    }

}