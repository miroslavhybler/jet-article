plugins {
    `kotlin-dsl`
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    id("com.android.test") version "8.2.0" apply false

    id("org.jetbrains.kotlin.android") version "1.9.21" apply false
    id("org.jetbrains.dokka") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("androidx.benchmark") version "1.1.1" apply false
}

java {
    withSourcesJar()
    withJavadocJar()
}