package com.jet.article.example.devblog.ui.main

import android.app.Application
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.viewModelScope
import com.jet.article.ArticleAnalyzer
import com.jet.article.ArticleParser
import com.jet.article.data.HtmlAnalyzerData
import com.jet.article.data.HtmlElement
import com.jet.article.data.TagInfo
import com.jet.article.example.devblog.ui.BaseViewModel
import com.jet.article.example.devblog.Constants
import com.jet.article.example.devblog.parseWithInitialization
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * @author Miroslav Hýbler <br>
 * created on 15.08.2024
 */
@HiltViewModel
class HomeListPaneViewModel @Inject constructor(
    application: Application,
) : BaseViewModel(
    application
) {


    private val mData: MutableStateFlow<List<PostItem>> = MutableStateFlow(value = emptyList())
    val data: StateFlow<List<PostItem>> = mData.asStateFlow()

    /**
     *
     */
    val lazyListState: LazyListState = LazyListState()

    fun loadIndexSite() {
        viewModelScope.launch {
            val htmlCode = loadFromUrl(url = Constants.indexUrl)

            val data = ArticleParser.parseWithInitialization(
                content = htmlCode,
                url = Constants.indexUrl,
            )

            ArticleAnalyzer.setInput(content = htmlCode)
            val links: ArrayList<TagInfo.Pair> = ArrayList()

            ArticleParser.initialize(
                isLoggingEnabled = true,
                areImagesEnabled = true,
                isSimpleTextFormatAllowed = true,
            )
            while (ArticleAnalyzer.moveInside()
                || ArticleAnalyzer.moveNext()
            ) {
                //Do nothing, wait until analyzer is done and then just take it's results
            }
            Log.d("mirek", "after while")

            val analyzerData = ArticleAnalyzer.resultData
                .filterIsInstance<HtmlAnalyzerData.ContentTag>()

            analyzerData.forEach { item ->
                val tag = item.tag
                if (tag is TagInfo.Pair) {
                    if (tag.tag == "a" && tag.clazz == "featured__href") {
                        links.add(element = tag)
                    }
                }
            }

            val chunked = data.elements.chunked(size = 4)
            val finalData = chunked.map { sublist ->
                PostItem(
                    image = (sublist[0] as HtmlElement.Image),
                    title = (sublist[1] as HtmlElement.TextBlock).text,
                    time = (sublist[2] as HtmlElement.TextBlock).text,
                    description = (sublist[3] as HtmlElement.TextBlock).text,
                    url = "TODO",
                )
            }
            Log.d("mirek", "links: ${links.size}")
            Log.d("mirek", "finalData: ${finalData.size}")
            mData.value = finalData
        }
    }


    data class PostItem constructor(
        val title: String,
        val url: String,
        val time: String,
        val description: String,
        val image: HtmlElement.Image,
    )
}