package dev.theseuntaylor.arcade

/**
 * A game playable in the arcade. [id] doubles as the URL fragment the arcade routes on (`/play/#<id>`), so it must
 * stay stable once a game ships.
 */
data class ArcadeGame(
    val id: String,
    val title: String,
    val tagline: String,
    val emoji: String,
    val playable: Boolean,
)

object Arcade {
    val games = listOf(
        ArcadeGame(
            id = "stackboxes",
            title = "StackBoxes",
            tagline = "Watch the boxes light up, then tap them back in order.",
            emoji = "🟩",
            playable = false,
        ),
        ArcadeGame(
            id = "hexo",
            title = "Hexo",
            tagline = "Tic-Tac-Toe against an AI that goes all the way up to Devilish.",
            emoji = "❌",
            playable = false,
        ),
    )

    fun find(id: String): ArcadeGame? = games.firstOrNull { it.id == id }
}
