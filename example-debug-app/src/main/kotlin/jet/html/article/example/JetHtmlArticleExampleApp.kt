package jet.html.article.example

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


/**
 * @author Miroslav Hýbler <br>
 * created on 13.12.2023
 */
@HiltAndroidApp
class JetHtmlArticleExampleApp constructor() : Application() {
    override fun onCreate() {
        super.onCreate()
    }

}