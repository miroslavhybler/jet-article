@file:Suppress("ConstPropertyName")

package com.jet.article.example.devblog

import android.app.Application
import android.app.NotificationManager
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.jet.article.example.devblog.data.ContentSyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


/**
 * @author Miroslav Hýbler <br>
 * created on 13.08.2024
 */
@HiltAndroidApp
class AndroidDevBlogApp : Application(),  Configuration.Provider {


    companion object {
        const val notificationGroupId: String = "default-group"
        const val notificationNewPostsChannelId: String = "new-posts"
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        prepareNotificationsGroupAndChannel()
        ContentSyncWorker.register(context = this)
        System.loadLibrary("jet-article")
    }


    private fun prepareNotificationsGroupAndChannel() {
        val defaultGroup = NotificationChannelGroupCompat.Builder(notificationGroupId)
            .setName(getString(R.string.ntfc_def_group_name))
            .setDescription(getString(R.string.ntfc_def_group_desc))
            .build()

        val newPostsChannel = NotificationChannelCompat.Builder(
            notificationNewPostsChannelId,
            NotificationManager.IMPORTANCE_MIN
        )
            .setName(getString(R.string.ntfc_def_channel_name))
            .setDescription(getString(R.string.ntfc_def_channel_desc))
            .setGroup(notificationGroupId)
            .setShowBadge(true)
            .setSound(null, null)
            .setVibrationPattern(null)
            .setVibrationEnabled(false)
            .build()

        val manager = NotificationManagerCompat.from(this)

        manager.createNotificationChannelGroup(defaultGroup)
        manager.createNotificationChannel(newPostsChannel)
    }
}