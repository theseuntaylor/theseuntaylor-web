package dev.theseuntaylor.site

import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.color

/**
 * Editorial palette: warm paper, soft ink, one loud accent. Swap [accent] to re-theme the whole site.
 * The arcade deliberately breaks from this with its own night palette.
 */
object SiteColors {
    val paper = Color.rgb(0xF4F0EC)
    val ink = Color.rgb(0x262626)
    val inkSoft = Color.rgba(38, 38, 38, 0.62f)
    val hairline = Color.rgba(38, 38, 38, 0.14f)
    val accent = Color.rgb(0x7F52FF) // Kotlin purple
    val accentSoft = Color.rgba(0x7F, 0x52, 0xFF, 0.12f)
    val green = Color.rgb(0x3DDC84) // Android green, used sparingly
    val night = Color.rgb(0x0B0816)
    val nightPanel = Color.rgb(0x15102A)
}

const val SerifFont = "Playfair Display"
const val SansFont = "Instrument Sans"
const val MonoFont = "JetBrains Mono"

@InitSilk
fun initTheme(ctx: InitSilkContext) {
    ctx.config.initialColorMode = ColorMode.LIGHT
    ctx.theme.palettes.light.background = SiteColors.paper
    ctx.theme.palettes.light.color = SiteColors.ink
    ctx.theme.palettes.dark.background = SiteColors.paper
    ctx.theme.palettes.dark.color = SiteColors.ink
}
