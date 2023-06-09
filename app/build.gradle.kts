plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.apaluk.streamtheater"
    compileSdk = Config.compileSdkVersion

    defaultConfig {
        applicationId = "com.apaluk.streamtheater"
        minSdk = Config.minSdkVersion
        targetSdk = Config.compileSdkVersion
        versionCode = Config.versionCode
        versionName = Config.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(Dependencies.AndroidX.core)
    implementation(Dependencies.AndroidX.lifecycleRuntime)
    implementation(Dependencies.AndroidX.lifecycleProcess)
    implementation(Dependencies.AndroidX.activityCompose)

    implementation(Dependencies.Compose.ui)
    implementation(Dependencies.Compose.uiToolingPreview)
    implementation(Dependencies.Compose.material)
    implementation(Dependencies.Compose.navigation)

    implementation(Dependencies.retrofit)
    implementation(Dependencies.guava)
    implementation(Dependencies.simpleXml)
    implementation(Dependencies.timber)
    implementation(Dependencies.okHttpLoggingInterceptor)
    implementation(Dependencies.datastore)
    implementation(Dependencies.coil)
    implementation(Dependencies.accompanist)

    implementation(Dependencies.Hilt.hilt)
    implementation(Dependencies.Hilt.navigationCompose)
    kapt(Dependencies.Hilt.hiltCompiler)
    kapt(Dependencies.Hilt.daggerHiltCompiler)

    implementation(Dependencies.Moshi.moshi)
    implementation(Dependencies.Moshi.converter)
    implementation(Dependencies.Moshi.adapters)
    kapt(Dependencies.Moshi.codegen)

    implementation(Dependencies.ExoPlayer.exoPlayer)
    implementation(Dependencies.ExoPlayer.ui)
    implementation(Dependencies.ExoPlayer.okHttpDataSource)

    ksp(Dependencies.Room.kotlinCompiler)
    implementation(Dependencies.Room.runtime)
    annotationProcessor(Dependencies.Room.kotlinCompiler)
    implementation(Dependencies.Room.ktx)

    testImplementation(Dependencies.Test.jUnit)
    testImplementation(Dependencies.Test.mockk)
    testImplementation(Dependencies.Test.hilt)
    testImplementation(Dependencies.Test.truth)
    testImplementation(Dependencies.Test.coroutines)
    androidTestImplementation(Dependencies.AndroidTest.jUnitExt)
    androidTestImplementation(Dependencies.AndroidTest.espresso)
    androidTestImplementation(Dependencies.AndroidTest.composeUiTest)
    androidTestImplementation(Dependencies.Test.truth)
    debugImplementation(Dependencies.Debug.composeUi)
    debugImplementation(Dependencies.Debug.composeUiTooling)
}