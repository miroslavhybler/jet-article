package com.jet.article.example.devblog

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


/**
 * @author Miroslav Hýbler <br>
 * created on 13.08.2024
 */
@HiltAndroidApp
class AndroidDevBlogApp : Application(){

    override fun onCreate() {
        super.onCreate()
    }
}