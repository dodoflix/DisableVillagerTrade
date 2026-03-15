pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
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
