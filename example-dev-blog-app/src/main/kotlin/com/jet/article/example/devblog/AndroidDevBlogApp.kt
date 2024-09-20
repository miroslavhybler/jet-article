@file:Suppress("ConstPropertyName")

package com.jet.article.example.devblog

import android.app.Application
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.CachePolicy
import com.jet.article.example.devblog.data.ContentSyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


/**
 * @author Miroslav Hýbler <br>
 * created on 13.08.2024
 */
@HiltAndroidApp
class AndroidDevBlogApp : Application(), Configuration.Provider, ImageLoaderFactory {


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


    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()

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