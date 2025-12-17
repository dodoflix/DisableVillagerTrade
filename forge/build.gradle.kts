plugins {
    id("net.minecraftforge.gradle") version "[6.0.16,6.2)"
    alias(libs.plugins.shadow)
}

// Extract versions from catalog for use in plugin DSLs
val minecraftVersion = libs.versions.minecraft.get()
val forgeVersion = libs.versions.forge.get()

base {
    archivesName.set("DisableVillagerTrade-Forge")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
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
}

dependencies {
    minecraft(libs.forge)
    
    // Include common module
    implementation(project(":common"))
    shadow(project(":common"))
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
