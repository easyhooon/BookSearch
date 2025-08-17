rootProject.name = "BookSearch"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

include(
    ":app",

    ":core:network",
    ":core:database",
    ":core:data",
    ":core:common",
    ":core:designsystem",
    ":core:domain",
    ":core:navigation",
    ":core:ui",

    ":feature:search",
    ":feature:favorites",
    ":feature:detail",
    ":feature:main",
)
