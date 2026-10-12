plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    // js: read by the Kobweb site (the arcade lobby lists games from here).
    // wasmJs: the arcade itself.
    // jvm: so the Android apps (Hexo, StackBoxes) can depend on the same game logic later.
    js { browser() }
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs { browser() }
    jvm()
}
