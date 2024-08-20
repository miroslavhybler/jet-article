package com.jet.article.example.devblog.data

import android.content.Context
import com.jet.article.ArticleAnalyzer
import com.jet.article.ArticleParser
import com.jet.article.data.HtmlAnalyzerData
import com.jet.article.data.TagInfo
import com.jet.article.example.devblog.Constants
import com.jet.article.example.devblog.data.database.PostItem
import com.jet.article.example.devblog.getPostList
import com.jet.article.example.devblog.parseWithInitialization
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okio.IOException
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


/**
 * @author Miroslav Hýbler <br>
 * created on 20.08.2024
 */
@Singleton
class CoreRepo @Inject constructor(
    @ApplicationContext context: Context,
) {

    companion object {
        private const val TIME_OUT = 5_000
    }

    private val mPosts: MutableStateFlow<List<PostItem>> = MutableStateFlow(value = emptyList())
    val posts: StateFlow<List<PostItem>> = mPosts.asStateFlow()


    private val ktorHttpClient: HttpClient = HttpClient(
        engineFactory = Android,
        block = {
            install(HttpCache) {
                val cacheFile = File(context.cacheDir, "http_cache")
                privateStorage(storage = FileStorage(directory = cacheFile))

            }
            engine {
                this.connectTimeout = TIME_OUT
                this.socketTimeout = TIME_OUT
            }
        }
    )


    suspend fun loadPosts(
        isRefresh: Boolean = false,
    ) {
        val htmlCode = loadHtmlFromUrl(
            url = Constants.indexUrl,
            isRefresh = isRefresh
        ) ?: return

        val data = ArticleParser.parseWithInitialization(
            content = htmlCode,
            url = Constants.indexUrl,
        )

        ArticleAnalyzer.setInput(content = htmlCode)
        val links: ArrayList<TagInfo.Pair> = ArrayList()
        val allLinks: ArrayList<TagInfo.Pair> = ArrayList()

        ArticleParser.initialize(
            isLoggingEnabled = true,
            areImagesEnabled = true,
            isSimpleTextFormatAllowed = true,
        )
        while (ArticleAnalyzer.moveNext()) {
            //Do nothing, wait until analyzer is done and then just take it's results
        }

        val analyzerData = ArticleAnalyzer.resultData
            .filterIsInstance<HtmlAnalyzerData.ContentTag>()

        analyzerData.forEach { item ->
            val tag = item.tag
            if (tag is TagInfo.Pair) {
                if (tag.tag == "a") {
                    allLinks.add(element = tag)
                    if (tag.clazz == "featured__href") {
                        links.add(element = tag)
                    }
                }
            }
        }

        val finalData = data.getPostList()
        mPosts.value = finalData
    }


    suspend fun loadHtmlFromUrl(
        url: String,
        isRefresh: Boolean = false
    ): String? {
        try {
            val response: HttpResponse = ktorHttpClient.get {
                url(urlString = url)
                if (isRefresh) {
                    parameter(
                        key = "refresh",
                        value = System.currentTimeMillis()
                    )
                }
            }
            return response.bodyAsText()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}