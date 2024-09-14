@file:OptIn(JetExperimental::class)

package com.jet.article.example.devblog.data

import android.content.Context
import android.util.Log
import com.jet.article.ArticleAnalyzer
import com.jet.article.ArticleParser
import com.jet.article.data.HtmlArticleData
import com.jet.article.data.HtmlElement
import com.jet.article.data.TagInfo
import com.jet.article.example.devblog.Constants
import com.jet.article.example.devblog.data.database.DatabaseRepo
import com.jet.article.example.devblog.data.database.PostItem
import com.jet.article.example.devblog.getPostList
import com.jet.article.example.devblog.parseWithInitialization
import com.jet.article.example.devblog.processDate
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
import mir.oslav.jet.annotations.JetExperimental
import okio.IOException
import org.joda.time.DateTime
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
    private val databaseRepo: DatabaseRepo,
) {

    companion object {
        private const val TIME_OUT = 5_000
    }

    private val mPosts: MutableStateFlow<Result<List<PostItem>>?> = MutableStateFlow(value = null)
    val posts: StateFlow<Result<List<PostItem>>?> = mPosts.asStateFlow()

    private val mHasErrorFromRemote: MutableStateFlow<Boolean> = MutableStateFlow(value = false)
    val hasErrorFromRemote: StateFlow<Boolean> = mHasErrorFromRemote.asStateFlow()

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
        if (isRefresh) {
            val result = loadPostsFromRemote()
            when {
                result.isSuccess -> {
                    mPosts.value = Result.success(value = databaseRepo.postDao.getAll())
                }

                result.isFailure -> {
                    mPosts.value = Result.failure(exception = result.exceptionOrNull()!!)
                }
            }


            return@withContext
        }
        mPosts.value = Result.success(value = databaseRepo.postDao.getAll())
    }


    suspend fun loadPostDetail(
        url: String,
        isRefresh: Boolean = false,
    ): Result<AdjustedPostData> = withContext(context = Dispatchers.IO) {
        val htmlCode = loadHtmlFromUrl(
            url = url,
            isRefresh = isRefresh
        )

        if (htmlCode == null) {
            return@withContext Result.failure(exception = NullPointerException("Html code is null"))
        }

        val original = ArticleParser.parseWithInitialization(
            content = htmlCode,
            url = url,
        )

        try {
            val title = original.elements
                .first { it is HtmlElement.Title } as HtmlElement.Title
            val headerImage = original.elements
                .first { it is HtmlElement.Image } as HtmlElement.Image
            val date = original.elements
                .first { it is HtmlElement.TextBlock } as HtmlElement.TextBlock


            val simpleDate = processDate(date = date.text)
            val newElements = ArrayList(original.elements).apply {
                remove(element = headerImage)
                remove(element = title)
                remove(element = date)
            }

            if (simpleDate == null) {
                mHasErrorFromRemote.value = true
                return@withContext Result.failure(
                    exception = NullPointerException("Unable to get date")
                )
            }

            mHasErrorFromRemote.value = false
            return@withContext Result.success(
                value = AdjustedPostData(
                    postData = original.copy(elements = newElements),
                    headerImage = headerImage,
                    date = simpleDate,
                    title = title,
                )
            )
        } catch (e: NoSuchElementException) {
            e.printStackTrace()
            return@withContext Result.failure(
                exception = NoSuchElementException("Unable to adjust html data")
            )
        }
    }


    suspend fun loadPostsFromRemote(): Result<List<PostItem>> = withContext(
        context = Dispatchers.IO
    ) {
        val htmlCode = loadHtmlFromUrl(
            url = Constants.indexUrl,
            isRefresh = true,
        )

        if (htmlCode == null) {
            mHasErrorFromRemote.value = true
            return@withContext Result.failure(
                exception = NullPointerException("Html code is null")
            )
        }

        val data = ArticleParser.parseWithInitialization(
            content = htmlCode,
            url = Constants.indexUrl,
        )

        val links: ArrayList<TagInfo> = ArrayList()

        ArticleParser.initialize(
            isLoggingEnabled = false,
            areImagesEnabled = true,
            isSimpleTextFormatAllowed = true,
            isQueringTextOutsideTextTags = true,
        )

        ArticleAnalyzer.process(
            content = htmlCode,
            onTag = { tag ->
                if (tag.tag == "a" && tag.clazz == "adb-card__href") {
                    links.add(element = tag)
                }
            }
        )

        val finalData = data.getPostList(links = links)
        mHasErrorFromRemote.value = finalData.isFailure
        return@withContext finalData
    }


    private suspend fun loadHtmlFromUrl(
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