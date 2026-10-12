package dev.theseuntaylor.site.components.sections

import com.varabyte.kobweb.compose.ui.thenIf
import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Column
import org.jetbrains.compose.web.dom.Span
import com.varabyte.kobweb.silk.style.toAttrs
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.css.PointerEvents
import com.varabyte.kobweb.silk.style.breakpoint.displayUntil
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import dev.theseuntaylor.site.components.widgets.ColorModeToggle
import dev.theseuntaylor.site.components.widgets.rememberLocalTime
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.style.toModifier
import dev.theseuntaylor.site.NavLinkStyle
import dev.theseuntaylor.site.SerifFont
import dev.theseuntaylor.site.SiteColors
import dev.theseuntaylor.site.data.Me
import org.jetbrains.compose.web.css.*

val NavBarStyle = CssStyle {
    base {
        Modifier
            .position(Position.Fixed)
            .top(0.px).left(0.px).right(0.px)
            .zIndex(10)
            .padding(topBottom = 1.4.cssRem, leftRight = 1.25.cssRem)
            .gap(0.7.cssRem)
            .fontSize(0.8.cssRem)
            // Paper fades in behind the links so content scrolling underneath stays legible.
            .styleModifier {
                property("background", "linear-gradient(color-mix(in srgb, var(--paper) 95%, transparent) 60%, transparent)")
            }
    }
    // Leave room on the right for the vertical wordmark.
    Breakpoint.MD { Modifier.padding(top = 2.cssRem, right = 5.cssRem, bottom = 2.cssRem, left = 2.cssRem).gap(2.4.cssRem).fontSize(1.cssRem) }
}

/**
 * The right edge on wider screens: one vertical line reading "theseuntaylor | london, uk | 22:43:11" from the top,
 * and the day/night toggle at the foot of the same column.
 */
val EdgeStyle = CssStyle.base {
    Modifier
        .position(Position.Fixed)
        .top(1.75.cssRem)
        .bottom(1.5.cssRem)
        .right(1.1.cssRem)
        .zIndex(11)
        .pointerEvents(PointerEvents.None) // the column is tall; only its contents should catch clicks
}

val EdgeLineStyle = CssStyle.base {
    Modifier.styleModifier {
        property("writing-mode", "vertical-rl")
        property("white-space", "nowrap")
    }
}

val WordmarkStyle = CssStyle.base {
    Modifier
        .fontFamily(SerifFont, "Georgia", "serif")
        .fontSize(1.45.cssRem)
        .color(SiteColors.accent)
        .textDecorationLine(TextDecorationLine.None)
        .pointerEvents(PointerEvents.Auto)
}

/** Location and time continue the wordmark's line in the same serif and size, just quieter in colour. */
val EdgeClockStyle = CssStyle.base {
    Modifier
        .fontFamily(SerifFont, "Georgia", "serif")
        .fontSize(1.45.cssRem)
        .color(SiteColors.inkSoft)
        .styleModifier { property("font-variant-numeric", "tabular-nums") } // seconds tick without the text jiggling
}

val navItems = listOf(
    "about" to "/#about",
    "work" to "/#work",
    "arcade" to "/arcade",
    "photography" to "/photography",
    "connect" to "/#connect",
)

@Composable
fun Nav(currentPath: String) {
    Row(NavBarStyle.toModifier(), verticalAlignment = Alignment.CenterVertically) {
        navItems.forEach { (label, href) ->
            val active = href == currentPath
            Link(href, label, NavLinkStyle.toModifier().fontSize(1.em).thenIf(active, Modifier.color(SiteColors.accent)))
        }
        Spacer()
        // Phones have no edge column, so the toggle sits at the end of the nav instead.
        ColorModeToggle(Modifier.displayUntil(Breakpoint.MD))
    }
    // The right edge; hidden on phones where it would sit on top of content.
    Column(
        EdgeStyle.toModifier().displayIfAtLeast(Breakpoint.MD),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Span(EdgeLineStyle.toAttrs()) {
            Link("/", Me.wordmark, WordmarkStyle.toModifier())
            Me.location?.let { location ->
                val time = rememberLocalTime(location)
                SpanText(
                    "  |  ${location.city.lowercase()} ${location.flag}  |  $time",
                    EdgeClockStyle.toModifier().attrsModifier { attr("data-edge-clock", "") },
                )
            }
        }
        ColorModeToggle(Modifier.pointerEvents(PointerEvents.Auto))
    }
}
