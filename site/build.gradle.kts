import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import kotlinx.html.link

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.application)
}

group = "dev.theseuntaylor.site"
version = "1.0-SNAPSHOT"

kobweb {
    app {
        index {
            description.set("Seun Taylor: mobile engineer, Kotlin Multiplatform enthusiast, photographer.")
            head.add {
                link(rel = "preconnect", href = "https://fonts.googleapis.com")
                link(rel = "stylesheet", href = "https://fonts.googleapis.com/css2?family=Instrument+Sans:wght@400;500;600&family=JetBrains+Mono:wght@500;700&family=Playfair+Display:ital,wght@0,400;0,500;1,400&display=swap")
            }
        }
    }
}

kotlin {
    configAsKobwebApplication("site")

    sourceSets {
        jsMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
            implementation(libs.silk.icons.fa)
            implementation(project(":arcade:shared"))
        }
    }
}

// The Compose Wasm arcade is built separately and synced into a generated resource folder at public/play/. Registering
// it as a resource source (not just a processResources input) matters: the dev server reads processed resources, but
// `kobwebExport` reads resource source folders. The distribution task carries index.html and compose resources; the
// webpack task carries arcade.js and the .wasm binaries.
evaluationDependsOn(":arcade:web")
val arcadeTasks = project(":arcade:web").tasks
val syncArcade = tasks.register<Sync>("syncArcade") {
    from(arcadeTasks.named("wasmJsBrowserDistribution"), arcadeTasks.named("wasmJsBrowserProductionWebpack"))
    exclude("**/*.map", "**/*.LICENSE.txt", "webpack.config.js")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    into(layout.buildDirectory.dir("generated/arcade/public/play"))
}
kotlin.sourceSets.named("jsMain") {
    resources.srcDir(syncArcade.map { layout.buildDirectory.dir("generated/arcade").get() })
}
