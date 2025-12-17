plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

base {
    archivesName.set("DisableVillagerTrade-Bukkit")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigotmc-repo"
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-snapshots"
    }
}

dependencies {
    implementation(project(":common"))
    
    compileOnly(libs.spigot.api)
    
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.spigot.api)
}

tasks {
    jar {
        archiveClassifier.set("slim")
    }
    
    shadowJar {
        archiveClassifier.set("")
        relocate("me.dodo.disablevillagertrade.common", "me.dodo.disablevillagertrade.bukkit.common")
        
        // Exclude duplicate LICENSE files to avoid Paper remapper issues
        exclude("LICENSE")
        exclude("LICENSE.txt")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        
        // Merge service files
        mergeServiceFiles()
    }
    
    build {
        dependsOn(shadowJar)
    }
    
    processResources {
        val props = mapOf(
            "version" to project.version,
            "description" to "A lightweight plugin to prevent players from trading with villagers."
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
    
    test {
        useJUnitPlatform()
    }
}
