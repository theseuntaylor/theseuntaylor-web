pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "theseuntaylor-web"

// The portfolio itself: real HTML/CSS, rendered with Kobweb (Compose HTML).
include(":site")

// The arcade: game logic shared with the mobile apps, plus a Compose Multiplatform (Wasm) shell that plays them.
include(":arcade:shared")
include(":arcade:web")
