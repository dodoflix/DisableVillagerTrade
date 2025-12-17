plugins {
    `java-library`
}

base {
    archivesName.set("disablevillagertrade-common")
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
