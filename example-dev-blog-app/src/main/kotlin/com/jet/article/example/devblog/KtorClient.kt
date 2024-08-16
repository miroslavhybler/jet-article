package com.jet.article.example.devblog

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.DefaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders

/**
 * @author Miroslav Hýbler <br>
 * created on 16.08.2024
 */
private const val TIME_OUT = 5_000

val ktorHttpClient = HttpClient(Android) {
    install(JsonFeature) {
        engine {
            connectTimeout = TIME_OUT
            socketTimeout = TIME_OUT
        }
    }
}