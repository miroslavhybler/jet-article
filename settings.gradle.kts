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
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.github.com/miroslavhybler/Maven") {
            val githubProperties = Properties()
            githubProperties.load(FileInputStream(File(rootDir, "github.properties")))
            val username = githubProperties["github.username"].toString()
            val token = githubProperties["github.token"].toString()

            credentials {
                this.username = username
                this.password = token
            }
        }
    }
}

rootProject.name = "Jet Html Article"
include(":jet-html-article")
include(":example-app")
include(":benchmark")
