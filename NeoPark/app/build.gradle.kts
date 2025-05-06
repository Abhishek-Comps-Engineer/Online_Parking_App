plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    alias(libs.plugins.google.gms.google.services) // Used for secrets management
}

android {
    namespace = "com.example.abhishek.financetracker.expensemanager.neopark"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.abhishek.financetracker.expensemanager.neopark"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Core Android Libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Play Services for Maps and Location
    implementation("com.google.android.gms:play-services-maps:18.0.2") // Google Maps
    implementation("com.google.android.gms:play-services-location:18.0.0") // Location services

    // Firebase Dependencies using BOM

    // Glide for Image Loading
    implementation("com.github.bumptech.glide:glide:4.13.0")
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.legacy.support.v4)
    implementation(libs.recyclerview)
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.0")

    // Lottie for Animations
    implementation("com.airbnb.android:lottie:6.1.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Testing Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    dependencies {
        // Espresso for UI Testing
        androidTestImplementation(libs.espresso.core)

        // Retrofit for Networking
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation("com.squareup.okhttp3:okhttp:4.9.3")
        implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

        // Room Database
        implementation("androidx.room:room-runtime:2.6.1")
        annotationProcessor("androidx.room:room-compiler:2.6.1")
        implementation("androidx.room:room-ktx:2.6.1")

        // SQLCipher for Encrypted Database
        implementation("net.zetetic:android-database-sqlcipher:4.5.3")

        // Firebase Dependencies
        implementation("com.google.firebase:firebase-firestore:24.10.0")
        implementation("com.google.firebase:firebase-storage:20.3.0")

        // Coroutine Support (For async tasks)
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")


    }

}
