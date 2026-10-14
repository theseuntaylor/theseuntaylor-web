package dev.theseuntaylor.site.data

/** Everything that reads as "copy" lives here, so editing the site rarely means touching layout code. */
object Me {
    const val name = "Solomon George-Taylor"
    const val wordmark = "theseuntaylor"
    const val role = "Mobile Software Engineer"
    const val email = "seun.gtaylor@gmail.com"
    const val github = "https://github.com/theseuntaylor"
    const val githubLabel = "github.com/theseuntaylor"
    const val googlePlay = "https://play.google.com/store/apps/developer?id=George-Taylor+Solomon"
    const val googlePlayLabel = "Apps on Google Play"

    /** Shown in the connect section with a live local clock. Leave null to hide it. */
    val location: Location? = Location("London, UK", "🇬🇧", "Europe/London")

    val intro = listOf(
        "Solomon builds apps you can tap. Mostly Kotlin and Jetpack Compose these days, with a growing love " +
            "for Kotlin Multiplatform and native SwiftUI on the iOS side.",
        "When he isn't squashing bugs, he's behind a camera, deep in a mind-puzzling game, or lost in a book or podcast.",
    )

    val toolbox = listOf(
        "Kotlin", "Jetpack Compose", "Kotlin Multiplatform", "Compose Multiplatform", "SwiftUI",
        "Firebase", "Spring Boot (learning)", "Gradle",
    )
}

class Location(val city: String, val flag: String, val timeZone: String)

class Project(
    val title: String,
    val summary: String,
    val stack: List<String>,
    val href: String,
    val badge: String? = null,
    val emoji: String,
)

val featuredProjects = listOf(
    Project(
        title = "theseuntaylor.dev",
        summary = "This site. A Kobweb portfolio with an arcade of my games running as Compose Multiplatform " +
            "compiled to WebAssembly, sharing Kotlin with the mobile apps.",
        stack = listOf("Kotlin", "Kobweb", "Compose Multiplatform", "Wasm"),
        href = "https://github.com/theseuntaylor/theseuntaylor-web",
        emoji = "🕹️",
    ),
    Project(
        title = "Hexo",
        summary = "Real-time multiplayer Tic-Tac-Toe with an AI that goes from Easy to Devilish.",
        stack = listOf("Kotlin", "Compose", "Firestore", "Hilt"),
        href = "https://github.com/theseuntaylor/Hexo",
        badge = "Google Play",
        emoji = "❌⭕",
    ),
    Project(
        title = "Book Awards",
        summary = "Literary award nominees, a reading tracker and announcement-day alerts. Compose on Android, " +
            "SwiftUI on iOS, one shared Kotlin core.",
        stack = listOf("KMP", "Compose Multiplatform", "SwiftUI"),
        href = "https://github.com/theseuntaylor/book-awards",
        emoji = "📚",
    ),
    Project(
        title = "StackBoxes",
        summary = "A Simon-style memory game across 3×3, 4×4 and 5×5 grids, with lives, streaks and a leaderboard.",
        stack = listOf("Kotlin", "Compose"),
        href = "https://github.com/theseuntaylor/StackBoxes",
        badge = "Google Play",
        emoji = "🟩",
    ),
    Project(
        title = "PicSome",
        summary = "A gallery of photos from the Picsum API, with favourites saved for offline viewing.",
        stack = listOf("Kotlin", "Compose", "Retrofit", "Room"),
        href = "https://github.com/theseuntaylor/PicSome",
        badge = "Google Play",
        emoji = "🖼️",
    ),
)
