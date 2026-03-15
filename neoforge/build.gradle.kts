plugins {
    alias(libs.plugins.neoforge.moddev)
}

val modVersion: String by project
val mavenGroup: String by project
group = mavenGroup
version = modVersion

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

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Jar> {
    from(rootDir.parentFile.resolve("LICENSE")) {
        rename { "${it}_DisableVillagerTrade" }
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
    mavenCentral()
}

dependencies {
    // common module is substituted by the includeBuild in settings.gradle.kts
    implementation("me.dodo:disablevillagertrade-common")
    jarJar("me.dodo:disablevillagertrade-common")
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
