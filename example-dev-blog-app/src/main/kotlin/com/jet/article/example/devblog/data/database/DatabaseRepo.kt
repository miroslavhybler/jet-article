package com.jet.article.example.devblog.data.database

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


/**
 * @author Miroslav Hýbler <br>
 * created on 20.08.2024
 */
@Singleton
class DatabaseRepo @Inject constructor(
    @ApplicationContext context: Context,
){

    private val database = LocalDatabase.create(context = context)

    val postDao: LocalDatabase.PostDao
        get() = database.postDao
}