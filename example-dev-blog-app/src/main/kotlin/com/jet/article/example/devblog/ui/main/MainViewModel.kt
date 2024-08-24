package com.jet.article.example.devblog.ui.main

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.jet.article.data.HtmlArticleData
import com.jet.article.example.devblog.data.AdjustedPostData
import com.jet.article.example.devblog.ui.BaseViewModel
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


    private val mPostData: MutableStateFlow<AdjustedPostData?> = MutableStateFlow(value = null)
    val postData: StateFlow<AdjustedPostData?> = mPostData.asStateFlow()

    fun loadPost(url: String, isRefresh: Boolean = false) {
        viewModelScope.launch {
            mPostData.value = coreRepo.loadPostDetail(url = url, isRefresh = isRefresh)
        }
    }


    fun onBack() {
        mPostData.value = null
    }
}