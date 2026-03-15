plugins {
    java
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

// Applies to `common` (the only subproject)
subprojects {
    apply(plugin = "java")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    repositories {
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

// JaCoCo for common
configure(subprojects.filter { it.name == "common" }) {
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
