package com.jet.article.example.devblog.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


/**
 * @author Miroslav Hýbler <br>
 * created on 16.08.2024
 */
@Singleton
class CacheRepo @Inject constructor(
    @ApplicationContext val context: Context,
) {
}