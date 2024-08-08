@file:Suppress("UnstableApiUsage")

import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.DokkaConfiguration.Visibility

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.dokka")
    id("maven-publish")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "mir.oslav.jet.html.article"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                cppFlags("-std=c++20")
            }
        }
    }
    sourceSets {
        getByName("androidTest") {
            assets.srcDir("src/androidTest/assets")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlin {
        jvmToolchain(jdkVersion = 8)
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    publishing {
        multipleVariants {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {

    api("com.github.miroslavhybler:jet-lint:1.0.2")
    implementation("com.github.miroslavhybler:jet-utils:1.1.2")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.startup:startup-runtime:1.1.1")

    /** Compose */
    val composeVersion = "1.6.8"
    implementation("androidx.compose.ui:ui:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.animation:animation-graphics:$composeVersion")
    implementation("androidx.activity:activity-compose:1.9.1")

    val material3Version = "1.2.1"
    implementation("androidx.compose.material3:material3:$material3Version")
    implementation("androidx.compose.material3:material3-window-size-class:$material3Version")


    /** Accompanist & Experimental */
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.0")

    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("net.danlew:android.joda:2.12.7")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

tasks {

    dokkaHtml.configure {
        outputDirectory.set(buildDir.resolve("docs/kotlin"))
        dokkaSourceSets {

            configureEach {
                pluginsMapConfiguration.set(
                    mutableMapOf(
                        "org.jetbrains.dokka.base.DokkaBase" to """{ "separateInheritedMembers": true}"""
                    )
                )

                documentedVisibilities.set(
                    mutableListOf(
                        Visibility.PUBLIC,
                        Visibility.PRIVATE,
                        Visibility.PROTECTED,
                        Visibility.INTERNAL,
                        Visibility.PACKAGE
                    )
                )

                skipEmptyPackages.set(true)
                includeNonPublic.set(true)
                skipDeprecated.set(false)
                reportUndocumented.set(true)
                includes.from("${projectDir}/packages.md")
            }
        }
    }


    create(name = "generateCppDocs", type = Exec::class) {
        val outputDir = buildDir.resolve(relative = "docs/cpp")
        if (!outputDir.exists()) { outputDir.mkdirs() }
        commandLine("doxygen")
    }


    create(name ="generateFullDocs", type = Task::class) {
        dependsOn("dokkaHtml", "generateCppDocs")
    }
}