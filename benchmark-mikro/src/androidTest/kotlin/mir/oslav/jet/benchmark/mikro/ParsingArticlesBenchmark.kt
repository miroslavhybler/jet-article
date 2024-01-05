package mir.oslav.jet.benchmark.mikro

import android.content.Context
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import mir.oslav.jet.html.JetHtmlArticleParserOld
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * @author Miroslav Hýbler <br>
 * created on 06.12.2023
 */
@RunWith(AndroidJUnit4::class)
class ParsingArticlesBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun parseArticle1() {
        benchmarkRule.measureRepeated {
            val context: Context = ApplicationProvider.getApplicationContext()
            val article = String(context.assets.open("mapbox.html").readBytes())
            JetHtmlArticleParserOld.parse(content = article)
        }
    }
}