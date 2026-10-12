package dev.theseuntaylor.site.components.sections

import com.varabyte.kobweb.compose.ui.thenIf
import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.foundation.layout.Row
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
            .gap(1.1.cssRem)
            .flexWrap(FlexWrap.Wrap)
            // Paper fades in behind the links so content scrolling underneath stays legible.
            .styleModifier {
                property("background", "linear-gradient(rgba(244,240,236,.95) 60%, rgba(244,240,236,0))")
            }
    }
    Breakpoint.MD { Modifier.padding(topBottom = 2.cssRem, leftRight = 2.cssRem).gap(2.4.cssRem) }
}

val WordmarkStyle = CssStyle.base {
    Modifier
        .position(Position.Fixed)
        .top(1.75.cssRem)
        .right(1.5.cssRem)
        .zIndex(11)
        .fontFamily(SerifFont, "Georgia", "serif")
        .fontSize(1.45.cssRem)
        .color(SiteColors.accent)
        .textDecorationLine(TextDecorationLine.None)
        .styleModifier { property("writing-mode", "vertical-rl") }
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
            Link(href, label, NavLinkStyle.toModifier().thenIf(active, Modifier.color(SiteColors.accent)))
        }
    }
    // The vertical name on the right edge; hidden on phones where it would sit on top of content.
    Link("/", Me.wordmark, WordmarkStyle.toModifier().displayIfAtLeast(Breakpoint.MD))
}
