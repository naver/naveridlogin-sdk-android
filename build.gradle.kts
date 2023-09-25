plugins {
    // // jcenter upload from https://github.com/bintray/gradle-bintray-plugin#readme
    id("com.jfrog.bintray") version "1.8.5"
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(Configurations.Plugins.android)
        classpath(Configurations.Plugins.kotlin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
