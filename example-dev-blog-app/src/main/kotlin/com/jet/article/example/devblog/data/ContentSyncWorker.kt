package com.jet.article.example.devblog.data

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.jet.article.ArticleAnalyzer
import com.jet.article.ArticleParser
import com.jet.article.data.TagInfo
import com.jet.article.example.devblog.Constants
import com.jet.article.example.devblog.data.database.DatabaseRepo
import com.jet.article.example.devblog.getPostList
import com.jet.article.example.devblog.parseWithInitialization
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Load index site on the background and loads articles from it
 * @author Miroslav Hýbler <br>
 * created on 16.08.2024
 */
@HiltWorker
class ContentSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private var databaseRepo: DatabaseRepo,
    private var coreRepo: CoreRepo,
) : CoroutineWorker(
    appContext = context,
    params = workerParameters,
) {



    companion object {
        fun register(context: Context) {
//            val request = getRequest()
//            val workManager = WorkManager.getInstance(context)
//
//            workManager.enqueueUniquePeriodicWork(
//                "update-post-list",
//                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
//                request,
//            )
        }

        private fun getRequest(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<ContentSyncWorker>(
                repeatInterval = 7,
                repeatIntervalTimeUnit = TimeUnit.DAYS,
            )
                .setConstraints(
                    constraints = Constraints.Builder()
                        .setRequiredNetworkType(networkType = NetworkType.CONNECTED)
                        .build()
                )
                .setInputData(Data.Builder().putBoolean("", true).build())
                .build()
        }

    }


    override suspend fun doWork(): Result {
        val htmlCode = coreRepo.loadHtmlFromUrl(url = Constants.indexUrl) ?: return Result.failure()

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
        ArticleAnalyzer.process(
            content = htmlCode,
            onTag = { tag ->
                if (tag.tag == "a" && tag.clazz == "featured__href") {
                    links.add(element = tag)
                }
            }
        )
        val finalData = data.getPostList(links = links)

        if (finalData.isEmpty()) {
            return Result.failure()
        }

        val dao = databaseRepo.postDao
        finalData.forEach { post ->
            if (!dao.contains(url = post.url)) {
                dao.insert(item = post)
            }
        }


        return Result.success()
    }
}