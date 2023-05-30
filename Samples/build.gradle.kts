plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = Configurations.compileSdkVersion
    buildToolsVersion = Configurations.buildToolsVersion

    defaultConfig {
        applicationId = "com.navercorp.nid.oauth.sample"

        targetSdk = Configurations.targetSdkVersion
        minSdk = Configurations.minSdkVersion

        versionName = Configurations.moduleVersionName
        versionCode = Configurations.moduleVersionCode

        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"),"proguard-rules.pro")
        }
        getByName("debug") {}
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    lint.isAbortOnError = false
    lint.isCheckReleaseBuilds = false

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    /* 네아로 SDK from Project */
    implementation(project(":Nid-OAuth"))

    Dependencies.Kotlin.run {
        implementation(stdLib)
        implementation(coroutines)
    }

    /* 네아로 SDK from Maven */
//    implementation 'com.naver.nid:naveridlogin-android-sdk:4.2.6'

    /* leakCanary */
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.9.1")

    implementation("androidx.multidex:multidex:2.0.1")

    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.1")
}