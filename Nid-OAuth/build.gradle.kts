import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}

android {
    compileSdk = Configurations.compileSdkVersion

    defaultConfig {
        minSdk = Configurations.minSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = false
        buildConfigField("String", "VERSION_NAME", "\"${Configurations.moduleVersionName}\"")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles("proguard-rules.pro", "proguard-rules-consumer.pro")
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    lint {
        abortOnError = false
        targetSdk = Configurations.targetSdkVersion
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
        targetSdk = Configurations.targetSdkVersion
    }

    namespace = "com.nhn.android.oauth"
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.legacy.support.core.utils)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.process)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.moshi.kotlin)
    implementation(libs.okhttp.logging.interceptor)

    api(libs.lottie)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.test.runner)
    testImplementation(libs.powermock.api.mockito2)
    testImplementation(libs.powermock.module.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.mockk)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
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
                        developer {
                            id.set("yuri.mun")
                            name.set("Yuri Mun")
                            email.set("yu_ri.mun@navercorp.com")
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