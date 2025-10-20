object Configurations {
    /* Kotlin */
    const val kotlinVersion = "2.1.0"

    /* Module */
    const val compileSdkVersion = 35
    const val targetSdkVersion = 35
    const val minSdkVersion = 21

    /* Version */
    const val moduleVersionName = "5.11.0"
    const val moduleVersionCode = 5_11_00

    /* Project ClassPath */
    object Plugins {
        const val android = "com.android.tools.build:gradle:8.6.1"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    }
}