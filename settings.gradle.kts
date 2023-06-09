import java.net.URI

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = URI.create("https://jitpack.io") }
        maven { url = URI.create("https://plugins.gradle.org/m2/") }
    }
}
rootProject.name = "stream-theater"
include(":app")
