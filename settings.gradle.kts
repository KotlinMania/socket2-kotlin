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
// socket2-kotlin's commonMain types (SockaddrStorage, Msghdr, Iovec, AF_*
// constants) are defined locally in LibcTypes.kt; no sibling dependency.
rootProject.name = "socket2-kotlin"
