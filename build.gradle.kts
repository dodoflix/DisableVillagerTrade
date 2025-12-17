plugins {
    java
    id("com.gradleup.shadow") version "9.3.0" apply false
    jacoco
}

jacoco {
    toolVersion = "0.8.12"
}

repositories {
    mavenCentral()
}

val modVersion: String by project
val mavenGroup: String by project

allprojects {
    group = mavenGroup
    version = modVersion
}

subprojects {
    apply(plugin = "java")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    repositories {
        // Minecraft libraries repo for LWJGL natives (needed for Forge on macOS)
        maven("https://libraries.minecraft.net/") {
            name = "Minecraft Libraries"
            content {
                includeGroup("org.lwjgl")
            }
        }
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Jar> {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_${rootProject.name}" }
        }
    }
}

// Apply JaCoCo only to testable modules (common, bukkit)
configure(subprojects.filter { it.name in listOf("common", "bukkit") }) {
    apply(plugin = "jacoco")
    
    tasks.withType<JacocoReport> {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    tasks.withType<Test> {
        finalizedBy(tasks.withType<JacocoReport>())
    }
}
