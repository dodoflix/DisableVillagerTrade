plugins {
    alias(libs.plugins.fabric.loom)
}

// Extract versions from catalog for use in tasks
val minecraftVersion = libs.versions.minecraft.get()
val fabricLoaderVersion = libs.versions.fabric.loader.get()

base {
    archivesName.set("DisableVillagerTrade-Fabric")
}

repositories {
    maven("https://maven.fabricmc.net/")
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
    
    implementation(project(":common"))
    include(project(":common"))
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
