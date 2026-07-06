pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    // Auto-provisions whatever JDK the toolchain needs, independent of the host JDK.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

includeBuild("../common") {
    dependencySubstitution {
        substitute(module("me.dodo:disablevillagertrade-common")).using(project(":"))
    }
}

rootProject.name = "bukkit"
