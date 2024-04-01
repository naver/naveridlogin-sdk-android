object Configurations {
    /* Kotlin */
    const val kotlinVersion = "1.6.21"

    /* Module */
    const val compileSdkVersion = 33
    const val buildToolsVersion = "30.0.2"
    const val targetSdkVersion = 33
    const val minSdkVersion = 21

    /* Version */
    const val moduleVersionName = "5.9.1"
    const val moduleVersionCode = 5_09_01

    /* Project ClassPath */
    object Plugins {
        const val android = "com.android.tools.build:gradle:7.4.0"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    }

}