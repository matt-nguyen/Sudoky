package com.nghianguyen.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

/** Enables Compose and aligns its versions without adding optional UI libraries. */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            pluginManager.withPlugin("com.android.library") {
                extensions.configure<LibraryExtension> { buildFeatures.compose = true }
                addComposeBom()
            }
            pluginManager.withPlugin("com.android.application") {
                extensions.configure<ApplicationExtension> { buildFeatures.compose = true }
                addComposeBom()
            }
        }
    }

    private fun Project.addComposeBom() {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        val bom = libs.findLibrary("androidx-compose-bom").get()
        dependencies.add("implementation", dependencies.platform(bom))
        dependencies.add("androidTestImplementation", dependencies.platform(bom))
    }
}
