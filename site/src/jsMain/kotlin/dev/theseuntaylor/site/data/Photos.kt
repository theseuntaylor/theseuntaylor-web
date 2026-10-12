package dev.theseuntaylor.site.data

/**
 * The photography gallery. To add a shot: drop the file in `site/src/jsMain/resources/public/photos/` and add a
 * [Photo] whose [src] is `/photos/<file>`. [width]/[height] only need the right ratio; they reserve space so the
 * masonry doesn't jump while images load.
 *
 * Entries with a null [src] render as tinted placeholder frames until real photos replace them.
 */
class Photo(
    val src: String?,
    val title: String,
    val place: String,
    val year: Int,
    val width: Int,
    val height: Int,
    val tint: String = "#D9CFC4",
)

val photos = listOf(
    Photo(null, "Golden hour", "Somewhere warm", 2026, 4, 5, tint = "#E3C9A8"),
    Photo(null, "Concrete & sky", "City", 2026, 3, 2, tint = "#C7CCD6"),
    Photo(null, "Market colours", "Street", 2025, 4, 5, tint = "#E2B7A4"),
    Photo(null, "Quiet water", "Coast", 2025, 1, 1, tint = "#B9CFCB"),
    Photo(null, "Night lines", "Downtown", 2025, 2, 3, tint = "#B6ADCB"),
    Photo(null, "Portrait study", "Studio", 2024, 4, 5, tint = "#D8C3BD"),
    Photo(null, "Leaves", "Park", 2024, 3, 2, tint = "#BFD0B0"),
    Photo(null, "Rooftops", "Old town", 2024, 4, 5, tint = "#D6C7B5"),
)
