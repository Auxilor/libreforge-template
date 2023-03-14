plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "8.0.0"
}

group = "com.willfp"
version = findProperty("version")!!
val libreforgeVersion = findProperty("libreforge-version")

base {
    archivesName.set(project.name)
}

dependencies {
    project(":eco-core").dependencyProject.subprojects {
        implementation(this)
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenLocal()
        mavenCentral()

        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://jitpack.io")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.codemc.org/repository/nms/")
        maven("https://repo.codemc.org/repository/maven-public")
        maven("https://repo.dmulloy2.net/nexus/repository/public/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://mvn.lumine.io/repository/maven-public/")
    }

    dependencies {
        compileOnly("com.willfp:eco:6.52.2")
        compileOnly("org.jetbrains:annotations:23.0.0")
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")

        compileOnly("com.willfp:libreforge:$libreforgeVersion")
        implementation("com.willfp:libreforge-loader:$libreforgeVersion")
    }

    java {
        withSourcesJar()
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    tasks {
        shadowJar {
            relocate("com.willfp.libreforge.loader", "com.willfp.libreforgetemplate.libreforge.loader")
        }

        compileKotlin {
            kotlinOptions {
                jvmTarget = "17"
            }
        }

        compileJava {
            options.isDeprecation = true
            options.encoding = "UTF-8"

            dependsOn(clean)
        }

        processResources {
            filesMatching(listOf("**plugin.yml", "**eco.yml")) {
                expand(
                    "version" to project.version,
                    "libreforgeVersion" to libreforgeVersion,
                    "pluginName" to rootProject.name
                )
            }
        }

        build {
            dependsOn(shadowJar)
        }
    }
}

tasks {
    val libreforgeJar = task("libreforgeJar", Jar::class) {
        destinationDirectory.set(file("$rootDir/bin"))
        archiveFileName.set("${project.name} v${project.version}.jar")

        dependsOn(shadowJar)

        from(
            configurations.compileClasspath.get()
                .filter { it.name.contains("libreforge-4") }
                    + shadowJar.get().outputs.files.map { zipTree(it) }
        )
    }

    build {
        dependsOn(libreforgeJar)
    }

    clean {
        doLast("Delete bin folder") {
            file("$rootDir/bin").deleteRecursively()
        }
    }
}
