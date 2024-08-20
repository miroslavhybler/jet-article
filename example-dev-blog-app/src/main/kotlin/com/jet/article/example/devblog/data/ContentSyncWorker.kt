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
import com.jet.article.ArticleParser
import com.jet.article.example.devblog.Constants
import com.jet.article.example.devblog.data.database.DatabaseRepo
import com.jet.article.example.devblog.getPostList
import com.jet.article.example.devblog.parseWithInitialization
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import java.util.concurrent.TimeUnit


/**
 * Load index site on the background and loads articles from it
 * @author Miroslav Hýbler <br>
 * created on 16.08.2024
 */
@HiltWorker
class ContentSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val databaseRepo: DatabaseRepo,
    private val coreRepo: CoreRepo,
) : CoroutineWorker(
    appContext = context,
    params = workerParameters,
) {


    companion object {
        fun register(context: Context) {
            val request = getRequest()
            val workManager = WorkManager.getInstance(context)

            workManager.enqueueUniquePeriodicWork(
                "update-post-list",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                request
            )

        }

        private fun getRequest(): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(networkType = NetworkType.CONNECTED)
                .build()

            val builder = PeriodicWorkRequestBuilder<ContentSyncWorker>(
                repeatInterval = 7,
                repeatIntervalTimeUnit = TimeUnit.DAYS,
            )

            return builder
                .setConstraints(constraints = constraints)
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
        val finalData = data.getPostList()


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