pluginManagement {
    repositories {
        maven("https://maven.neoforged.net/releases/") { name = "NeoForge" }
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    // Auto-provisions the JDK 25 toolchain (required by NeoForge 26.1.2+) on machines that don't have it installed.
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

rootProject.name = "neoforge"
