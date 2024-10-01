// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    id("com.google.devtools.ksp") version "2.0.20-1.0.25" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("androidx.room") version "2.6.1" apply false


    id("com.mikepenz.aboutlibraries.plugin") version "11.2.3" apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false

//    id("com.chaquo.python") version "15.0.1" apply false

}
