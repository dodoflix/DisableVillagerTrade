pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.architectury.dev/") { name = "Architectury" }
        maven("https://maven.minecraftforge.net/") { name = "Forge" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForge" }
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "DisableVillagerTrade"

include("common")
include("bukkit")
include("fabric")
include("neoforge")

// Forge doesn't work on macOS due to missing native libraries (lwjgl-freetype)
val isMacOS = System.getProperty("os.name").lowercase().contains("mac")
if (!isMacOS) {
    include("forge")
}
