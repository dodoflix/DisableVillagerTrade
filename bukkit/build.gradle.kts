plugins {
    `java-library`
    id("com.gradleup.shadow")
}

val spigotApiVersion: String by project

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
    
    compileOnly("org.spigotmc:spigot-api:$spigotApiVersion")
    
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:6.0.1")
    testImplementation("org.mockito:mockito-core:5.21.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.21.0")
    testImplementation("org.spigotmc:spigot-api:$spigotApiVersion")
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
