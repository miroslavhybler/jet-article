package jet.html.article.example.analyzer

import android.content.Context
import android.content.res.AssetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jet.article.ArticleAnalyzer
import com.jet.article.data.HtmlAnalyzerData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * @author Miroslav Hýbler <br>
 * created on 21.02.2024
 */
@HiltViewModel
class AnalyzerViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {


    private val assets: AssetManager = context.assets


    val analyzerFlow: StateFlow<HtmlAnalyzerData>
        get() = ArticleAnalyzer.analyzerFlow

    fun loadArticleFromResources(article: String) {
        viewModelScope.launch {
            val content = String(assets.open(article).readBytes())
            ArticleAnalyzer.setInput(content = content)
        }
    }


    fun doNextStep() {
        viewModelScope.launch {
            ArticleAnalyzer.moveNext()
        }
    }

    fun moveInside() {
        viewModelScope.launch {
            ArticleAnalyzer.moveInside()
        }
    }

}