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

subprojects {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}
