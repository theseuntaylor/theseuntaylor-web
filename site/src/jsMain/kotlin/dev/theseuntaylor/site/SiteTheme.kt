package dev.theseuntaylor.site

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.registerStyleBase
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.loadFromLocalStorage
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.systemPreference
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.Color as CssColor

/** Light ("day") and dark ("night") values for each themeable token, written out as CSS custom properties. */
private class Palette(val paper: String, val ink: String, val inkSoft: String, val hairline: String, val accent: String)

private val day = Palette(
    paper = "#F4F0EC",
    ink = "#262626",
    inkSoft = "rgba(38, 38, 38, 0.62)",
    hairline = "rgba(38, 38, 38, 0.14)",
    accent = "#7F52FF", // Kotlin purple
)

private val night = Palette(
    paper = "#141117",
    ink = "#EDE8E3",
    inkSoft = "rgba(237, 232, 227, 0.62)",
    hairline = "rgba(237, 232, 227, 0.14)",
    accent = "#A68BFF", // Kotlin purple, lifted so it holds contrast on the dark paper
)

/**
 * Editorial palette: warm paper, soft ink, one loud accent. The themeable tokens are CSS variables, so every style
 * flips with the colour mode without needing to know about it. The arcade keeps its own night palette in both modes.
 */
object SiteColors {
    val paper: CSSColorValue = CssColor("var(--paper)")
    val ink: CSSColorValue = CssColor("var(--ink)")
    val inkSoft: CSSColorValue = CssColor("var(--ink-soft)")
    val hairline: CSSColorValue = CssColor("var(--hairline)")
    val accent: CSSColorValue = CssColor("var(--accent)")

    /** Fixed colours, for alpha blends and for surfaces that look the same in both modes. */
    val accentFixed = Color.rgb(0x7F52FF)
    val onDark = Color.rgb(0xF4F0EC)
    val green = Color.rgb(0x3DDC84) // Android green, used sparingly
    val night = Color.rgb(0x0B0816)
}

const val SerifFont = "Playfair Display"
const val SansFont = "Instrument Sans"
const val MonoFont = "JetBrains Mono"

const val ColorModeStorageKey = "theseuntaylor-color-mode"

@InitSilk
fun initTheme(ctx: InitSilkContext) {
    // A saved choice wins; otherwise follow the visitor's system setting. Storage can throw in private windows.
    ctx.config.initialColorMode =
        runCatching { ColorMode.loadFromLocalStorage(ColorModeStorageKey) }.getOrNull() ?: ColorMode.systemPreference

    // Silk's Surface paints the page from its own palette, so mirror paper/ink there too.
    ctx.theme.palettes.light.background = Color.rgb(0xF4F0EC)
    ctx.theme.palettes.light.color = Color.rgb(0x262626)
    ctx.theme.palettes.dark.background = Color.rgb(0x141117)
    ctx.theme.palettes.dark.color = Color.rgb(0xEDE8E3)

    fun Palette.asVariables() = Modifier.styleModifier {
        property("--paper", paper)
        property("--ink", ink)
        property("--ink-soft", inkSoft)
        property("--hairline", hairline)
        property("--accent", accent)
        property("color-scheme", if (this@asVariables === night) "dark" else "light")
    }
    ctx.stylesheet.registerStyleBase(":root, .silk-light") { day.asVariables() }
    ctx.stylesheet.registerStyleBase(".silk-dark") { night.asVariables() }
}
