plugins {
    id("com.android.library")
    `maven-publish`
    kotlin("android")
}

android {
    compileSdk = Configurations.compileSdkVersion
    buildToolsVersion = Configurations.buildToolsVersion

//    testOptions.unitTests.includeAndroidResources = true

    defaultConfig {
        targetSdk = Configurations.targetSdkVersion
        minSdk = Configurations.minSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = false
        buildConfigField("String", "VERSION_NAME", "\"${Configurations.moduleVersionName}\"")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    lint.isAbortOnError = false

    buildFeatures {
        viewBinding = true
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {

    Dependencies.Kotlin.run {
        implementation(stdLib)
        implementation(coroutines)
    }

    Dependencies.AndroidX.run {
        implementation(appcompat)
        implementation(coreUtils)
        implementation(browser)
        implementation(supportV4)
        implementation(constraintLayout)
        implementation(crypto)
        implementation(coreKtx)
    }

    Dependencies.HttpClient.run {
        implementation(retrofit)
        implementation(converter)
        implementation(moshi)
        implementation(httpInterceptor)
    }

    Dependencies.AirBnB.run {
        api(lottie)
    }

    Dependencies.UnitTest.run {
        testImplementation(junit)
        testImplementation(androidxTestCore)
        testImplementation(androidxTestRunner)
        testImplementation(powerMockApi)
        testImplementation(powerMockJunit)
        testImplementation(robolectric)
        testImplementation(mockWebServer)
        testImplementation(mockK)
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                // Applies the component for the release build variant.
                from(components.getByName("release"))

                groupId = "com.navercorp.nid"
                artifactId = "oauth"
                version = Configurations.moduleVersionName

                pom {
                    licenses {
                        license {
                            name.set("Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.html")
                            distribution.set("repo")
                        }
                    }

                    developers {
                        developer {
                            id.set("namhun.kim")
                            name.set("Namhoon Kim")
                            email.set("namhun.kim@navercorp.com")
                        }
                        developer {
                            id.set("dayeon.lee")
                            name.set("Dayeon Lee")
                            email.set("dayeon.lee@navercorp.com")
                        }
                    }

                    scm {
                        connection.set("scm:git@github.com:naver/naveridlogin-sdk-android.git")
                        developerConnection.set("scm:git@github.com:naver/naveridlogin-sdk-android.git")
                        url.set("https://github.com/naver/naveridlogin-sdk-android")
                    }
                }
            }
        }
    }
}