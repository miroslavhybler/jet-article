plugins {
    `kotlin-dsl`
    id("com.android.application") version "8.4.1" apply false
    id("com.android.library") version "8.4.1" apply false
    id("com.android.test") version "8.4.1" apply false

    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("org.jetbrains.dokka") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false
    id("androidx.benchmark") version "1.2.4" apply false
}

java {
    withSourcesJar()
    withJavadocJar()
}