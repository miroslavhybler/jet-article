package com.jet.article.example.devblog.ui.main

import android.app.Application
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.viewModelScope
import com.jet.article.ArticleAnalyzer
import com.jet.article.ArticleParser
import com.jet.article.data.HtmlAnalyzerData
import com.jet.article.data.TagInfo
import com.jet.article.example.devblog.ui.BaseViewModel
import com.jet.article.example.devblog.Constants
import com.jet.article.example.devblog.data.database.PostItem
import com.jet.article.example.devblog.getPostList
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

    val posts: StateFlow<List<PostItem>> get() = coreRepo.posts

    /**
     *
     */
    val lazyListState: LazyListState = LazyListState()

    fun loadIndexSite(
        isRefresh: Boolean = false,
    ) {
        viewModelScope.launch {
            coreRepo.loadPosts(isRefresh=isRefresh)
        }
    }


}