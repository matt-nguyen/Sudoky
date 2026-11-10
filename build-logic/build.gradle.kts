import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.nghianguyen.buildlogic"

// Match AGP's minimum runtime without requiring a separate JDK installation.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.compose.gradle.plugin)
    implementation(libs.androidx.room.gradle.plugin)
    // KSP is applied by ID; its newer Kotlin metadata is not needed for compilation.
    runtimeOnly(libs.ksp.gradle.plugin)
    // Only extension APIs are needed when compiling the conventions.
    compileOnly(libs.detekt.gradle.plugin) { isTransitive = false }
    compileOnly(libs.ktfmt.gradle.plugin) { isTransitive = false }
    runtimeOnly(libs.detekt.gradle.plugin)
    runtimeOnly(libs.ktfmt.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("jvmLibrary") {
            id = "sudoky.jvm.library"
            implementationClass = "com.nghianguyen.buildlogic.JvmLibraryConventionPlugin"
        }
        register("quality") {
            id = "sudoky.quality"
            implementationClass = "com.nghianguyen.buildlogic.QualityConventionPlugin"
        }
        register("androidApplication") {
            id = "sudoky.android.application"
            implementationClass = "com.nghianguyen.buildlogic.AndroidApplicationConventionPlugin"
        }
        register("androidRoom") {
            id = "sudoky.android.room"
            implementationClass = "com.nghianguyen.buildlogic.AndroidRoomConventionPlugin"
        }
        register("androidCompose") {
            id = "sudoky.android.compose"
            implementationClass = "com.nghianguyen.buildlogic.AndroidComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "sudoky.android.library"
            implementationClass = "com.nghianguyen.buildlogic.AndroidLibraryConventionPlugin"
        }
    }
}
