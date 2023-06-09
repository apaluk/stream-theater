buildscript {

    dependencies {
        classpath(Dependencies.Hilt.gradlePlugin)
    }

}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version Versions.androidLibrary apply false
    id("com.android.library") version Versions.androidLibrary apply false
    id("org.jetbrains.kotlin.android") version Versions.kotlin apply false
    id("com.google.devtools.ksp") version Versions.ksp apply false
}