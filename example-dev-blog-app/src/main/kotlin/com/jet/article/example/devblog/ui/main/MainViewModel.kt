package com.jet.article.example.devblog.ui.main

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.jet.article.ArticleParser
import com.jet.article.data.HtmlArticleData
import com.jet.article.example.devblog.ui.BaseViewModel
import com.jet.article.example.devblog.parseWithInitialization
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * @author Miroslav Hýbler <br>
 * created on 14.08.2024
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
) : BaseViewModel(
    application
) {


    private val mHtmlData = MutableStateFlow(value = HtmlArticleData.empty)
    val htmlData: StateFlow<HtmlArticleData> = mHtmlData.asStateFlow()

    fun loadArticle(url: String) {
        viewModelScope.launch {
            mHtmlData.value = ArticleParser.parseWithInitialization(
                content = String(context.assets.open("article.html").readBytes()),
                url = "https://android-developers.googleblog.com/2024/07/the-fourth-beta-of-android-15.html",
            )
        }
    }


    fun onBack() {
        mHtmlData.value = HtmlArticleData.empty
    }
}