package com.jet.article.example.devblog.data

import android.content.Context
import android.util.Log
import com.jet.article.ArticleAnalyzer
import com.jet.article.ArticleParser
import com.jet.article.data.HtmlArticleData
import com.jet.article.data.HtmlElement
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
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
    ): Unit = withContext(context = Dispatchers.Default) {
        val htmlCode = loadHtmlFromUrl(
            url = Constants.indexUrl,
            isRefresh = isRefresh
        ) ?: return@withContext

        val data = ArticleParser.parseWithInitialization(
            content = htmlCode,
            url = Constants.indexUrl,
        )


        val links: ArrayList<TagInfo> = ArrayList()

        ArticleParser.initialize(
            isLoggingEnabled = false,
            areImagesEnabled = true,
            isSimpleTextFormatAllowed = true,
        )

        ArticleAnalyzer.jumpToBody()
        ArticleAnalyzer.process(
            content = htmlCode,
            onTag = { tag ->
                if (tag.tag == "a" && tag.clazz == "adb-card__href") {
                    links.add(element = tag)
                }
            }
        )
        val finalData = data.getPostList(links = links)
        mPosts.value = finalData
    }


    suspend fun loadPostDetail(
        url: String,
        isRefresh: Boolean = false,
    ): AdjustedPostData? = withContext(context = Dispatchers.IO) {
        val htmlCode = loadHtmlFromUrl(
            url = url,
            isRefresh = isRefresh
        ) ?: return@withContext null

        val original = ArticleParser.parseWithInitialization(
            content = htmlCode,
            url = Constants.indexUrl,
        )
        val title = original.elements
            .first { it is HtmlElement.Title } as HtmlElement.Title
        val headerImage = original.elements
            .first { it is HtmlElement.Image } as HtmlElement.Image
        val date = original.elements
            .first { it is HtmlElement.TextBlock } as HtmlElement.TextBlock

        val newElements = ArrayList(original.elements).apply {
            remove(headerImage)
            remove(title)
            remove(date)
        }

        return@withContext AdjustedPostData(
            postData = original.copy(elements = newElements),
            headerImage = headerImage,
            date = date,
            title = title,
        )
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