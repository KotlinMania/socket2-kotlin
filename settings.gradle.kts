pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

// rootProject.name only — no composite-build includes.
// socket2-kotlin consumes libc-kotlin from Maven Central at the catalog version.
rootProject.name = "socket2-kotlin"
