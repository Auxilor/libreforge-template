pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.jpenilla.xyz/snapshots/")
    }
}

rootProject.name = "libreforge-template"

// Core
include(":eco-core")
include(":eco-core:core-plugin")
