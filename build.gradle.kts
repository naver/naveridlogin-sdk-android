plugins {
    // // jcenter upload from https://github.com/bintray/gradle-bintray-plugin#readme
    id("com.jfrog.bintray") version "1.8.5"
}

buildscript {
    val kotlin_version by extra("1.5.20")
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(Configurations.Plugins.android)
        classpath(Configurations.Plugins.kotlin)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
