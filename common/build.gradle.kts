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
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:6.0.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    test {
        useJUnitPlatform()
    }
}

