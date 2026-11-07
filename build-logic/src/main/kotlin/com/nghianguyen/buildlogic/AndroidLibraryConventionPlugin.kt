package com.nghianguyen.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("sudoky.quality")
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.android")

            extensions.configure<LibraryExtension> {
                target.configureAndroid(this)
                namespace = "com.nghianguyen" + target.path.replace(':', '.')
                defaultConfig { consumerProguardFiles("consumer-rules.pro") }
            }
        }
    }
}
