@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.ManagedVirtualDevice

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.jet.article.test"
    compileSdk = 35

    defaultConfig {
        applicationId = "jet.html.article.example"
        minSdk = 21
        targetSdk = 35
        versionCode = 5
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
            isProfileable = true
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
        freeCompilerArgs += listOf(
            //Suppressing Jet Warnings
            "-Xopt-in=mir.oslav.jet.annotations.JetExperimental",
            "-Xopt-in=mir.oslav.jet.annotations.JetBenchmark",

            //Suppresing Material 3 Warnings
            "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-Xopt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-Xopt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-Xopt-in=androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi",
            "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi",
            "-Xopt-in=com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi"
        )
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {

            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    testOptions {
        managedDevices {
            devices {
                create(name = "pixel6Api32", type = ManagedVirtualDevice::class) {
                    device = "Pixel 6"
                    apiLevel = 32
                    systemImageSource = "aosp"
                }
            }
        }
    }
}

dependencies {

    implementation(project(":jet-article"))
    implementation(libs.androidx.core.core.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.runtime.ktx)
    implementation(libs.androidx.core.splashscreen)

    /** Compose */
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.androidx.activity.compose)
    implementation(libs.material3)
    implementation(libs.material3.window.size)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.accompanist.systemuicontroller)

    /** Hilt DI */
    implementation(libs.google.dagger.hilt)
    ksp(libs.google.dagger.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.gson)
}