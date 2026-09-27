plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.kwame.aikeyboard"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kwame.aikeyboard"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20240303")
    // Added for Phase 3 language-data tests (LanguageDataValidatorTest) — pure-JVM unit
    // tests, no emulator needed. Note: the existing GitHub Actions workflow only runs
    // assembleDebug, not test, so these won't run in CI until build.yml also runs
    // ./gradlew test — flagging that rather than silently assuming it's covered.
    testImplementation("junit:junit:4.13.2")
}
