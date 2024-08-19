package com.jet.article.example.devblog.data

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


/**
 * Load index site on the background and loads articles from it
 * @author Miroslav Hýbler <br>
 * created on 16.08.2024
 */
@HiltWorker
class ContentSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
) : CoroutineWorker(
    appContext = context,
    params = workerParameters,
) {


    companion object {
        fun register(context: Context) {
            TODO("Not yet implemented")

        }
    }


    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
        return Result.success()
    }
}