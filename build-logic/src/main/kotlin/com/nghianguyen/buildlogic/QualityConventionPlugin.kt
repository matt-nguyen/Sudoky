package com.nghianguyen.buildlogic

import com.ncorti.ktfmt.gradle.KtfmtExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/** Applies the repository's existing formatting style and static-analysis rules. */
class QualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.ncorti.ktfmt.gradle")
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            extensions.configure<KtfmtExtension> { kotlinLangStyle() }
            extensions.configure<DetektExtension> {
                config.setFrom(
                    target.rootProject.layout.projectDirectory.file("config/detekt/detekt.yml")
                )
                buildUponDefaultConfig = true
            }
        }
    }
}
