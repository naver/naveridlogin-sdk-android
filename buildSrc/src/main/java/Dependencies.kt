object Dependencies {

    object Kotlin {
        const val stdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Configurations.kotlinVersion}"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9"
    }

    const val androidxVersion = "1.0.0"
    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:$androidxVersion"
        const val coreUtils = "androidx.legacy:legacy-support-core-utils:$androidxVersion"
        const val browser = "androidx.browser:browser:$androidxVersion"
        const val supportV4 = "androidx.legacy:legacy-support-v4:$androidxVersion"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
        const val crypto = "androidx.security:security-crypto:1.1.0-alpha03"
        const val coreKtx = "androidx.core:core-ktx:1.3.0"
    }

    object HttpClient {
        const val retrofit = "com.squareup.retrofit2:retrofit:2.9.0"
        const val converter = "com.squareup.retrofit2:converter-gson:2.9.0"
        const val moshi = "com.squareup.moshi:moshi-kotlin:1.11.0"
        const val httpInterceptor = "com.squareup.okhttp3:logging-interceptor:4.2.1"
    }

    object AirBnB {
        const val lottie = "com.airbnb.android:lottie:3.1.0"
    }


    object UnitTest {
        const val junit = "junit:junit:4.12"
        const val androidxTestCore = "androidx.test:core:1.2.0"
        const val androidxTestRunner = "androidx.test:runner:1.2.0"
        const val powerMockApi = "org.powermock:powermock-api-mockito2:2.0.2"
        const val powerMockJunit = "org.powermock:powermock-module-junit4:2.0.2"
        const val robolectric = "org.robolectric:robolectric:4.3.1"
        const val mockWebServer = "com.squareup.okhttp3:mockwebserver:4.9.2"
        const val mockK = "io.mockk:mockk:1.12.2"
    }

    object AndroidTest {
        const val mockitoCore = "org.powermock:powermock-core:2.0.2"
        const val dexMaker = "com.google.dexmaker:dexmaker:1.2"
        const val dexMakerMockito = "com.google.dexmaker:dexmaker-mockito:1.2"
        const val assertJ = "com.squareup.assertj:assertj-android:1.0.0"
    }
}