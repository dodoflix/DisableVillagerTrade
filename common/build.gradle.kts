plugins {
    `java-library`
}

// Set group/version when this project is run as a standalone included build.
// When included as a root subproject these are set by root's allprojects block.
val modVersion: String by project
val mavenGroup: String by project
group = mavenGroup
version = modVersion

base {
    archivesName.set("disablevillagertrade-common")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
}

dependencies {
    // Common module has no platform-specific dependencies
    // Only pure Java code for shared logic

    // Test dependencies
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks {
    test {
        useJUnitPlatform()
    }
}
