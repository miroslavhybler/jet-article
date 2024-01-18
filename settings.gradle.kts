@file:Suppress("UnstableApiUsage")

import kotlin.collections.mutableMapOf
import java.io.FileInputStream
import kotlin.collections.mutableListOf
import java.util.Properties


pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "Jet Html Article"
include(":jet-html-article")
include(":benchmark-test-app")
include(":benchmark-mikro")
include(":example-test-app")