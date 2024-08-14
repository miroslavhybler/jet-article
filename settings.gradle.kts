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

rootProject.name = "Jet Article"
include(":jet-article")
include(":example-debug-app")
include(":example-dev-blog-app")