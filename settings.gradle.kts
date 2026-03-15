pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "DisableVillagerTrade"

// common is a regular subproject — shared logic and unit tests
include("common")

// Platform modules are independent builds, each with their own Gradle wrapper:
//   bukkit, forge, neoforge → gradle-8.14.3 (forge/gradle/wrapper/gradle-wrapper.properties)
//   fabric                  → gradle-9.2.0  (fabric/gradle/wrapper/gradle-wrapper.properties)
// Run them from their own directories: `cd fabric && ./gradlew build`
