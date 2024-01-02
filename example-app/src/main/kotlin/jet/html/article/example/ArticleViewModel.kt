package jet.html.article.example

import android.content.Context
import android.content.res.AssetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mir.oslav.jet.html.data.HtmlConfig
import mir.oslav.jet.html.data.HtmlData
import mir.oslav.jet.html.data.IgnoreOptions
import mir.oslav.jet.html.parse.JetHtmlArticleParser
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

    fun parse(config: HtmlConfig, ignoreOptions: IgnoreOptions, article: String) {
        viewModelScope.launch {
            JetHtmlArticleParser.parse(
                content = getArticle(fileName = article),
                config = config,
                ignoreOptions = ignoreOptions,
                isDoingMetering = true
            ).collect { newData ->
                mArticleData.value = newData
            }
        }
    }


    private fun getArticle(fileName: String): String {
        return String(assets.open("simple-examples/${fileName}.html").readBytes())
    }

}