// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false // For Android application projects
    alias(libs.plugins.google.gms.google.services) apply false // For Google Services (e.g., Firebase)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false // For Maps secrets management
}
