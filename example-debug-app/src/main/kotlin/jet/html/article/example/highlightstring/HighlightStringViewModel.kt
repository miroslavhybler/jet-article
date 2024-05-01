package jet.html.article.example.highlightstring

import android.content.Context
import android.content.res.AssetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * @author Miroslav Hýbler <br>
 * created on 30.04.2024
 */
@HiltViewModel
class HighlightStringViewModel @Inject constructor(
    @ApplicationContext context: Context,
) : ViewModel() {

    private val mArticleData: MutableStateFlow<String> =
        MutableStateFlow(value = "")
    val articleData: StateFlow<String> get() = mArticleData

    private var articlePath: String = ""

    private val assets: AssetManager = context.assets

    fun loadArticleFromResources(
        article: String,
    ) {
        viewModelScope.launch {
            mArticleData.value = getArticleContent(fileName = article)
        }
    }


    private fun getArticleContent(fileName: String): String {
        articlePath = "$fileName"
        return String(assets.open(articlePath).readBytes())
    }
}