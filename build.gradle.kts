import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.1.20"
    id("com.github.johnrengelman.shadow").version("8.1.1")
}

group = "me.deadybbb"
version = "0.1"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven { url = uri("https://maven.maxhenkel.de/repository/public/") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("de.maxhenkel.voicechat:voicechat-api:2.5.27")
    compileOnly("dev.jorel:commandapi-bukkit-core:10.0.1")
}


tasks.withType<ShadowJar> {
    exclude("/META-INF/*.kotlin_module")
    exclude("**/kotlin/**")
    minimize()
}

kotlin {
    jvmToolchain(17)
}