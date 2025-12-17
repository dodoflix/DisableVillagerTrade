plugins {
    alias(libs.plugins.neoforge.moddev)
}

// Extract versions from catalog using explicit API (required for NeoForge plugin compatibility)
val catalog = versionCatalogs.named("libs")
val minecraftVersion = catalog.findVersion("minecraft").get().requiredVersion
val neoforgeVersion = catalog.findVersion("neoforge").get().requiredVersion

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
