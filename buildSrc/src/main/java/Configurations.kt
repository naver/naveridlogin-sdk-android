object Configurations {
    /* Kotlin */
    const val kotlinVersion = "1.6.10"

    /* Module */
    const val compileSdkVersion = 31
    const val buildToolsVersion = "30.0.2"
    const val targetSdkVersion = 31
    const val minSdkVersion = 21

    /* Version */
    const val moduleVersionName = "5.2.0"
    const val moduleVersionCode = 5_02_00

    /* Project ClassPath */
    object Plugins {
        const val android = "com.android.tools.build:gradle:7.0.0"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    }

}