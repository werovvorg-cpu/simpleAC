plugins { java }

group = "me.server"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-spigot:2.9.5")
}

// Плоская структура: все файлы лежат в корне репозитория
sourceSets {
    main {
        java {
            setSrcDirs(listOf("."))
            include("*.java")
        }
        resources {
            setSrcDirs(listOf("."))
            include("plugin.yml", "config.yml")
        }
    }
}

java { toolchain.languageVersion.set(JavaLanguageVersion.of(21)) }
tasks.withType<JavaCompile> { options.encoding = "UTF-8" }
