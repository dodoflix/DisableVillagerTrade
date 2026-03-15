plugins {
    id("net.minecraftforge.gradle") version "[6.0.16,6.2)"
    alias(libs.plugins.shadow)
}

val modVersion: String by project
val mavenGroup: String by project
group = mavenGroup
version = modVersion

// Extract versions from catalog for use in plugin DSLs
val forgeVersion = libs.versions.forge.get()
// Derive Minecraft version from Forge version (e.g., "1.21.11" from "1.21.11-61.0.3")
require('-' in forgeVersion) { "Unexpected Forge version format (expected '<mcVersion>-<forgeVersion>'): $forgeVersion" }
val minecraftVersion = forgeVersion.substringBefore('-')

base {
    archivesName.set("DisableVillagerTrade-Forge")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Jar> {
    from(rootDir.parentFile.resolve("LICENSE")) {
        rename { "${it}_DisableVillagerTrade" }
    }
}

minecraft {
    mappings("official", minecraftVersion)

    runs {
        create("client") {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            mods {
                create("disablevillagertrade") {
                    source(sourceSets.main.get())
                }
            }
        }

        create("server") {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            mods {
                create("disablevillagertrade") {
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

repositories {
    maven("https://maven.minecraftforge.net/")
    // Minecraft libraries repo for LWJGL natives (needed on macOS)
    maven("https://libraries.minecraft.net/") {
        name = "Minecraft Libraries"
        content { includeGroup("org.lwjgl") }
    }
    mavenCentral()
}

dependencies {
    minecraft(libs.forge)

    // common module is substituted by the includeBuild in settings.gradle.kts
    implementation("me.dodo:disablevillagertrade-common")
    shadow("me.dodo:disablevillagertrade-common")
}

tasks {
    processResources {
        val forgeVersionOnly = forgeVersion.split("-")[1]
        val props = mapOf(
            "version" to project.version,
            "minecraft_version" to minecraftVersion,
            "forge_version" to forgeVersionOnly
        )
        inputs.properties(props)
        filesMatching("META-INF/mods.toml") {
            expand(props)
        }
    }

    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.getByName("shadow"))
        relocate("me.dodo.disablevillagertrade.common", "me.dodo.disablevillagertrade.forge.common")

        // Exclude duplicate LICENSE files
        exclude("LICENSE")
        exclude("LICENSE.txt")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")

        // Merge service files
        mergeServiceFiles()

        manifest {
            attributes(
                "Specification-Title" to "DisableVillagerTrade",
                "Specification-Vendor" to "dodo",
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "dodo"
            )
        }
    }

    jar {
        archiveClassifier.set("slim")
    }

    build {
        dependsOn(shadowJar)
    }
}

// Configure reobfShadowJar if available (ForgeGradle adds reobf tasks after evaluation)
afterEvaluate {
    tasks.findByName("reobfShadowJar")?.let { reobfTask ->
        tasks.named("shadowJar") {
            finalizedBy(reobfTask)
        }
    }
    tasks.findByName("reobfJar")?.let { reobfTask ->
        tasks.named("shadowJar") {
            finalizedBy(reobfTask)
        }
    }
}
