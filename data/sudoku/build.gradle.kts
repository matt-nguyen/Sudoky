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
    implementation(project(":domain:sudoku"))
    implementation(project(":core:domain"))

    implementation(libs.kotlin.result)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)

    implementation(libs.androidx.core.ktx)
    //    implementation(libs.androidx.appcompat)
    //    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    //    androidTestImplementation(libs.androidx.espresso.core)
}
