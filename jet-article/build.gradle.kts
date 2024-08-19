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
        targetSdk = 34
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                arguments(
                    "-DANDROID_ARM_MODE=arm",
                    "-DANDROID_STL=c++_shared"
                )
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
        debug {
            externalNativeBuild {
                cmake {
                    arguments(
                        "-DANDROID_ARM_MODE=arm",
                        "-DANDROID_STL=c++_shared"
                    )
                }
            }
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

    api(libs.jet.lint)
    implementation(libs.jet.utils)

    implementation(libs.androidx.core.core.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.runtime.ktx)
    implementation(libs.androidx.startup.startup.runtime)

    /** Compose */
    implementation(libs.androidx.animation.graphics)
    implementation(libs.androidx.activity.compose)
    implementation(libs.material3)
    implementation(libs.material3.window.size)
    implementation(libs.androidx.ui.tooling.preview.android)

    /** Accompanist & Experimental */
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.systemuicontroller)

    api(libs.coil.compose)
    implementation(libs.material)
    implementation(libs.android.joda)

    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.ui.tooling)
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
                        Visibility.PACKAGE,
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
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        commandLine("doxygen")
    }


    create(name = "generateFullDocs", type = Task::class) {
        dependsOn("dokkaHtml", "generateCppDocs")
    }
}