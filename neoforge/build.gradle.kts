plugins {
    id("net.neoforged.moddev") version "2.0.124"
}

val minecraftVersion: String by project
val neoforgeVersion: String by project

base {
    archivesName.set("DisableVillagerTrade-NeoForge")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

neoForge {
    version = neoforgeVersion
    
    runs {
        create("client") {
            client()
        }
        create("server") {
            server()
        }
    }
    
    mods {
        create("disablevillagertrade") {
            sourceSet(sourceSets.main.get())
        }
    }
}

repositories {
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    implementation(project(":common"))
    jarJar(project(":common"))
}

tasks {
    processResources {
        val props = mapOf(
            "version" to project.version,
            "minecraft_version" to minecraftVersion,
            "neoforge_version" to neoforgeVersion
        )
        inputs.properties(props)
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(props)
        }
    }
}
