plugins {
    id("sudoky.android.library")
    alias(libs.plugins.koin.compiler)
}

android {
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":data:scanner"))
    implementation(project(":domain:sudoku"))

    implementation(libs.mlkit.textrecognition)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android) // try commenting this out?

    implementation(libs.kotlin.result)

    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}
