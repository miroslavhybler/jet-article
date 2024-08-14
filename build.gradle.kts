plugins {
    `kotlin-dsl`
    id("com.android.application") version "8.5.2" apply false
    id("com.android.library") version "8.5.2" apply false
    id("com.android.test") version "8.5.2" apply false

    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("org.jetbrains.dokka") version "1.9.20" apply false
    id("androidx.benchmark") version "1.2.4" apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.google.dagger.hilt) apply false
}

java {
    withSourcesJar()
    withJavadocJar()
}