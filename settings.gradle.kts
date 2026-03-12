pluginManagement {
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

rootProject.name = "Sudoky"
include(":app")
//include(":feature:base")
include(":feature:scanner")
include(":feature:sudoku")
include(":core:feature")
include(":domain:sudoku")
include(":core:domain")
include(":data:sudoku")
include(":data:sudoku:room")
include(":data:scanner")
include(":data:scanner:mlkit")
include(":feature:sudoku")
include(":core:ui")
include(":feature:home")
