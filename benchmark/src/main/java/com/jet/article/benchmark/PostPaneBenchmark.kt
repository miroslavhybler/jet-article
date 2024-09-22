package com.jet.article.benchmark

import android.content.Context
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * @author Miroslav Hýbler <br>
 * created on 17.09.2024
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class PostPaneBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    val context: Context
        get() = InstrumentationRegistry.getInstrumentation().context


    @Test
    fun loadPost() = benchmarkRule.measureRepeated(
        packageName = "com.jet.article.example.devblog",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD
    ) {

        //TODO
      //  device.findObject(By.text("JetHtmlArticle")).wait();
    }
}