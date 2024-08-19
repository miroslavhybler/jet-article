package com.jet.article.example.devblog.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.jet.article.example.devblog.ktorHttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText


/**
 * @author Miroslav Hýbler <br>
 * created on 15.08.2024
 */
abstract class BaseViewModel constructor(
    application: Application,
) : AndroidViewModel(
    application
) {

   protected val context: Context
        get() = getApplication()


    protected suspend fun loadFromUrl(url: String): String {
        val response: HttpResponse = ktorHttpClient.get(urlString = url)
        return response.readText()
    }
}