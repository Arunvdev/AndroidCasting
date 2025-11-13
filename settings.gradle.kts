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
        maven("https://4thline.org/m2")
    }
}

rootProject.name = "AndroidCasting"
include(":app")
include(":core")
include(":data")
include(":domain")
include(":player")
include(":ui")
