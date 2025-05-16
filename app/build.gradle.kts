plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // Dodaję kapt do przetwarzania adnotacji Room
}

android {
    namespace = "com.example.filmoteka"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.filmoteka"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1") // Odkomentowane dla obsługi kapt Room
    implementation("androidx.room:room-ktx:2.6.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Dodanie zależności Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7") // Zmiana z 2.9.0 na 2.7.7
    
    // Coil do ładowania obrazów w Jetpack Compose
    implementation("io.coil-kt:coil-compose:2.4.0")
    
    // Dodanie kotlinx-metadata-jvm, które pomoże z błędem
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.7.0")

}
