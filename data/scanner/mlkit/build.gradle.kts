import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.nghianguyen.data.scanner.mlkit"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }
    
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":domain:sudoku"))

    implementation(libs.mlkit.textrecognition)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android) // try commenting this out?

    implementation(libs.kotlin.result)

    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}