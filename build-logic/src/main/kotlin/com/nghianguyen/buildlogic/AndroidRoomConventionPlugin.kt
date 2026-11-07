package com.nghianguyen.buildlogic

import androidx.room.gradle.RoomExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

/** Configures Room processing and keeps schema exports in the owning module. */
class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("androidx.room")

            extensions.configure<RoomExtension> {
                schemaDirectory(target.layout.projectDirectory.dir("schemas"))
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies.add("implementation", libs.findLibrary("androidx-room-runtime").get())
            dependencies.add("implementation", libs.findLibrary("androidx-room-ktx").get())
            dependencies.add("ksp", libs.findLibrary("androidx-room-compiler").get())
        }
    }
}
