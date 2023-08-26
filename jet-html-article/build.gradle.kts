plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "mir.oslav.jet.html"
    compileSdk = 33

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
}

dependencies {

    implementation("mir.oslav.jet:annotations:1.0.0")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")

    /** Compose */
    val composeVersion = "1.5.0"
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.material:material:1.5.0")
    implementation("androidx.compose.material3:material3:1.2.0-alpha05")
    implementation("androidx.compose.material3:material3-window-size-class:1.1.1")
    implementation("androidx.navigation:navigation-compose:2.7.0")
    implementation("androidx.paging:paging-compose:3.2.0")
    implementation("androidx.compose.animation:animation-graphics:1.5.0")
    implementation("androidx.paging:paging-compose:3.2.0")


    /** Accompanist & Experimental */
    implementation("com.google.accompanist:accompanist-permissions:0.29.1-alpha")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.28.0")

    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("com.google.android.material:material:1.9.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}