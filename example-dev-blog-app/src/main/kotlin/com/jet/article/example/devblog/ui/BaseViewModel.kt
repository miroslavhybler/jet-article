package com.jet.article.example.devblog.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.jet.article.example.devblog.data.CoreRepo
import javax.inject.Inject


/**
 * @author Miroslav Hýbler <br>
 * created on 15.08.2024
 */
abstract class BaseViewModel constructor(
    application: Application,
) : AndroidViewModel(
    application
) {

    @Inject
    lateinit var coreRepo: CoreRepo

   protected val context: Context
        get() = getApplication()

}