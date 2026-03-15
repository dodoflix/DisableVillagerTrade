plugins {
    alias(libs.plugins.fabric.loom)
}

val modVersion: String by project
val mavenGroup: String by project
group = mavenGroup
version = modVersion

// Extract versions from catalog for use in tasks
val minecraftVersion = libs.versions.minecraft.get()
val fabricLoaderVersion = libs.versions.fabric.loader.get()

base {
    archivesName.set("DisableVillagerTrade-Fabric")
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

repositories {
    maven("https://maven.fabricmc.net/")
    mavenCentral()
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)

    // common module is substituted by the includeBuild in settings.gradle.kts
    implementation("me.dodo:disablevillagertrade-common")
    include("me.dodo:disablevillagertrade-common")
}

tasks {
    processResources {
        val props = mapOf(
            "version" to project.version,
            "minecraft_version" to minecraftVersion,
            "loader_version" to fabricLoaderVersion
        )
        inputs.properties(props)
        filesMatching("fabric.mod.json") {
            expand(props)
        }
    }
}
