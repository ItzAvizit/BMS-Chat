plugins {
    java
    id("io.github.goooler.shadow") version "8.1.7"
}

group = "net.bms.chatmanager"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://nexus.scarsz.me/content/groups/public/")
}

dependencies {
    // Paper API (Includes Folia API in later versions)
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    
    // PlaceholderAPI
    compileOnly("me.clip:placeholderapi:2.11.5")
    
    // LuckPerms
    compileOnly("net.luckperms:api:5.4")
    
    // DiscordSRV
    compileOnly("com.discordsrv:discordsrv:1.28.0")
    
    // Adventure API & MiniMessage
    implementation("net.kyori:adventure-api:4.16.0")
    implementation("net.kyori:adventure-text-minimessage:4.16.0")
    
    // Adventure Platform for Bukkit (to support legacy Spigot without native Adventure)
    implementation("net.kyori:adventure-platform-bukkit:4.3.2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    shadowJar {
        relocate("net.kyori", "net.bms.chatmanager.libs.kyori")
        archiveFileName.set("BMS-ChatManager.jar")
    }
    
    build {
        dependsOn(shadowJar)
    }
    
    processResources {
        val props = mapOf("version" to project.version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
