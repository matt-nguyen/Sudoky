plugins {
    id("sudoky.jvm.library")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(libs.kotlin.result)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
}
