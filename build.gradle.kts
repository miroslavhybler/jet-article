plugins {
    `kotlin-dsl`
    id("com.android.application") version "8.5.2" apply false
    id("com.android.library") version "8.5.2" apply false
    id("com.android.test") version "8.5.2" apply false

    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("org.jetbrains.dokka") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false
    id("androidx.benchmark") version "1.2.4" apply false
    alias(libs.plugins.compose.compiler) apply false
}

java {
    withSourcesJar()
    withJavadocJar()
}