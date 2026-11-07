# Build conventions

This included build shares the root version catalog, including the project's
unchanged AGP and Kotlin plugin versions. Run it using the root Gradle wrapper.

## Migration status

All six plain Android libraries now use `sudoky.android.library`:

- `:core:domain` (the original pilot)
- `:core:feature`
- `:domain:sudoku`
- `:data:scanner`
- `:data:sudoku`
- `:data:scanner:mlkit`

The three plain data libraries still apply the Koin compiler plugin explicitly.
`:core:ui`, `:feature:home`, `:feature:scanner`, and `:feature:sudoku` use both
`sudoky.android.library` and `sudoky.android.compose`. The feature modules
still apply Koin and serialization explicitly. `:data:sudoku:room` uses
`sudoky.android.library` and `sudoky.android.room`, with Koin explicit. The app
uses `sudoky.android.application` and `sudoky.android.compose`, keeping Koin and
serialization explicit.

The library convention applies Android Library and Kotlin Android and supplies:

- Namespace: `com.nghianguyen` plus the module path, with colons replaced by dots.
- Compile SDK 36 and minimum SDK 26.
- Java and Kotlin JVM target 11.
- AndroidJUnitRunner and `consumer-rules.pro`.

It adds no dependencies. Dependencies and release minification/ProGuard policy
remain explicit in each module.

The application convention applies Android Application and Kotlin Android,
uses the same SDK/compiler/test-runner defaults as libraries, and sets target
SDK 36. The shared defaults live in `AndroidConfiguration.kt`. The app keeps
its namespace exception, application ID, versioning, signing, release policy,
and dependencies explicit in its own build script.

The Compose convention applies the catalog-versioned Compose compiler plugin,
enables Compose, and adds the Compose BOM to `implementation` and
`androidTestImplementation`. It adds no optional UI, tooling, navigation, or
test libraries; those remain explicit in each module. Apply an Android library
or application plugin alongside the Compose convention.

The Room convention applies Room and KSP, adds the catalog's Room runtime and
KTX dependencies plus its KSP compiler, and exports schemas to the consuming
module's `schemas` directory. Apply the Android library convention first.
For `:data:sudoku:room`, this preserves `data/sudoku/room/schemas` and the
existing database class and version. Historical app schema exports are retained.

Both Android base conventions apply `sudoky.quality` before the Android/Kotlin
plugins. All twelve Android modules therefore receive the existing ktfmt
`kotlinLangStyle()` and Detekt rules from `config/detekt/detekt.yml`, with
`buildUponDefaultConfig` enabled. Quality checks keep failing on rule violations;
the migration adds no suppression or generated Detekt baseline.

The root build contains one `apply false` convention declaration to keep
tooling on a shared root classloader. Module configuration lives in conventions;
there are no root `subprojects` or `allprojects` configuration blocks. Empty
parent projects no longer receive unused quality tasks. The included build uses
a group-filtered Plugin Portal dependency repository for ktfmt's implementation.

For example:

```kotlin
plugins { id("sudoky.android.library") }

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}
```

## Verification

From the repository root:

```text
./gradlew :build-logic:build
./gradlew :core:domain:assembleDebug :core:domain:assembleRelease :core:domain:testDebugUnitTest :core:domain:assembleDebugAndroidTest
./gradlew :core:feature:assembleDebug :core:feature:assembleRelease :core:feature:testDebugUnitTest :domain:sudoku:assembleDebug :domain:sudoku:assembleRelease :domain:sudoku:testDebugUnitTest
./gradlew :data:scanner:assembleDebug :data:scanner:assembleRelease :data:scanner:testDebugUnitTest :data:sudoku:assembleDebug :data:sudoku:assembleRelease :data:sudoku:testDebugUnitTest :data:scanner:mlkit:assembleDebug :data:scanner:mlkit:assembleRelease :data:scanner:mlkit:testDebugUnitTest
./gradlew :core:ui:assembleDebug :core:ui:assembleRelease :core:ui:assembleDebugAndroidTest
./gradlew :feature:home:assembleDebug :feature:home:assembleRelease :feature:scanner:assembleDebug :feature:scanner:assembleRelease :feature:sudoku:assembleDebug :feature:sudoku:assembleRelease
./gradlew :data:sudoku:room:assembleDebug :data:sudoku:room:assembleRelease :data:sudoku:room:testDebugUnitTest :data:sudoku:room:assembleDebugAndroidTest
git diff --exit-code -- app/schemas data/sudoku/room/schemas
./gradlew :app:assembleDebug :app:assembleRelease testDebugUnitTest :app:assembleDebugAndroidTest
./gradlew :core:ui:dependencyInsight --configuration debugCompileClasspath --dependency androidx.compose:compose-bom
./gradlew :feature:scanner:dependencyInsight --configuration debugAndroidTestCompileClasspath --dependency androidx.compose:compose-bom
./gradlew detekt ktfmtCheck lintDebug --continue
./gradlew ktfmtCheckScripts
```

The included build's `build` task includes Gradle plugin validation. The pilot
currently has no JVM unit test sources, so its test task reports NO-SOURCE;
compiling its actual Android variants verifies integration with AGP and Kotlin.
Building an instrumentation APK does not execute tests on a device.

Keep a short Gradle user-home path on Windows: nested project-local caches can
exceed Windows path limits while unpacking Android dependencies.

The quality commands select tasks across the Android modules. `build-logic`
is validated separately by its own `build` task. Module-specific Detekt and lint
reports are generated under each module's `build/reports` directory.

## Migration complete

All five conventions are implemented: application, library, Compose, Room, and
quality. AGP and dependency versions remain unchanged by the migration.
