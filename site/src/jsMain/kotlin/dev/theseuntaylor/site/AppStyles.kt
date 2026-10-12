package dev.theseuntaylor.site

import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.css.TransitionTimingFunction
import com.varabyte.kobweb.compose.css.autoLength
import com.varabyte.kobweb.compose.css.FontStyle
import com.varabyte.kobweb.compose.css.ScrollBehavior
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.registerStyleBase
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.selectors.hover
import org.jetbrains.compose.web.css.*

@InitSilk
fun initSiteStyles(ctx: InitSilkContext) {
    ctx.stylesheet.registerStyle("html") {
        cssRule(CSSMediaQuery.MediaFeature("prefers-reduced-motion", StylePropertyValue("no-preference"))) {
            Modifier.scrollBehavior(ScrollBehavior.Smooth)
        }
    }
    ctx.stylesheet.registerStyleBase("html, body") { Modifier.fillMaxHeight() }
    ctx.stylesheet.registerStyleBase("body") {
        Modifier
            .fontFamily(SansFont, "-apple-system", "system-ui", "sans-serif")
            .fontSize(17.px)
            .lineHeight(1.65)
            .color(SiteColors.ink)
            .backgroundColor(SiteColors.paper)
            .styleModifier { property("-webkit-font-smoothing", "antialiased") }
    }
    ctx.stylesheet.registerStyleBase("::selection") { Modifier.backgroundColor(SiteColors.accent).color(SiteColors.paper) }
    ctx.stylesheet.registerStyleBase("[id]") { Modifier.styleModifier { property("scroll-margin-top", "6rem") } }

    // Scroll reveal: elements start slightly low and transparent, and Reveal() adds `.in` once they're on screen.
    ctx.stylesheet.registerStyle(".reveal") {
        base {
            Modifier
                .opacity(0)
                .translateY(28.px)
                .transition(
                    Transition.of("opacity", 900.ms, TransitionTimingFunction.cubicBezier(0.2, 0.7, 0.2, 1.0)),
                    Transition.of("transform", 900.ms, TransitionTimingFunction.cubicBezier(0.2, 0.7, 0.2, 1.0)),
                )
        }
        cssRule(CSSMediaQuery.MediaFeature("prefers-reduced-motion", StylePropertyValue("reduce"))) {
            Modifier.opacity(1).styleModifier { property("transform", "none") }
        }
    }
    ctx.stylesheet.registerStyleBase(".reveal.in") { Modifier.opacity(1).styleModifier { property("transform", "none") } }
}

/** The huge lowercase serif headings ("work", "arcade", "photography"). */
val DisplayHeadingStyle = CssStyle {
    base {
        Modifier
            .fontFamily(SerifFont, "Georgia", "serif")
            .fontWeight(400)
            .color(SiteColors.accent)
            .fontSize(4.25.cssRem)
            .lineHeight(1.0)
            .letterSpacing((-0.02).em)
            .margin(0.px, 0.px, 2.5.cssRem, 0.px)
    }
    Breakpoint.MD { Modifier.fontSize(7.5.cssRem) }
}

val SerifTitleStyle = CssStyle.base {
    Modifier
        .fontFamily(SerifFont, "Georgia", "serif")
        .fontWeight(400)
        .fontSize(1.6.cssRem)
        .lineHeight(1.2)
        .margin(0.px)
}

val LeadStyle = CssStyle {
    base {
        Modifier
            .fontFamily(SerifFont, "Georgia", "serif")
            .fontSize(1.45.cssRem)
            .lineHeight(1.45)
            .maxWidth(40.cssRem)
            .margin(0.px, 0.px, 1.5.cssRem, 0.px)
    }
    Breakpoint.MD { Modifier.fontSize(1.75.cssRem) }
}

val ItalicStyle = CssStyle.base { Modifier.fontStyle(FontStyle.Italic).color(SiteColors.accent) }

val EyebrowStyle = CssStyle.base {
    Modifier
        .fontSize(0.78.cssRem)
        .letterSpacing(0.18.em)
        .styleModifier { property("text-transform", "uppercase") }
        .color(SiteColors.inkSoft)
}

val MutedTextStyle = CssStyle.base { Modifier.color(SiteColors.inkSoft).fontSize(0.95.cssRem) }

/** Content column: generous side space on desktop, a 16px-ish gutter on phones, room for the vertical wordmark. */
val ContainerStyle = CssStyle {
    base { Modifier.fillMaxWidth().padding { leftRight(1.25.cssRem) }.margin { leftRight(autoLength) } }
    Breakpoint.MD { Modifier.maxWidth(72.cssRem).padding { leftRight(4.5.cssRem) } }
}

val SectionStyle = CssStyle {
    base { Modifier.padding { topBottom(5.cssRem) } }
    Breakpoint.MD { Modifier.padding { topBottom(8.cssRem) } }
}

/** A row in a hairline-divided list: title + detail on the left, a quiet meta column on the right. */
val ListRowStyle = CssStyle {
    base {
        Modifier
            .fillMaxWidth()
            .padding { topBottom(1.6.cssRem) }
            .borderBottom(1.px, LineStyle.Solid, SiteColors.hairline)
            .color(SiteColors.ink)
            .textDecorationLine(TextDecorationLine.None)
            .transition(Transition.of("padding", 300.ms, TransitionTimingFunction.EaseOut), Transition.of("color", 200.ms))
    }
    hover { Modifier.padding { left(0.75.cssRem) }.color(SiteColors.accent) }
}

val NavLinkStyle = CssStyle {
    base {
        Modifier
            .fontSize(0.92.cssRem)
            .color(SiteColors.inkSoft)
            .textDecorationLine(TextDecorationLine.None)
            .transition(Transition.of("color", 150.ms))
    }
    hover { Modifier.color(SiteColors.accent) }
}

val InlineLinkStyle = CssStyle {
    base {
        Modifier
            .color(SiteColors.ink)
            .textDecorationLine(TextDecorationLine.Underline)
            .styleModifier {
                property("text-decoration-color", "rgba(127,82,255,.4)")
                property("text-underline-offset", "4px")
            }
            .transition(Transition.of("color", 150.ms))
    }
    hover { Modifier.color(SiteColors.accent) }
}

val PillButtonStyle = CssStyle {
    base {
        Modifier
            .display(DisplayStyle.InlineBlock)
            .fontSize(0.92.cssRem)
            .fontWeight(500)
            .padding(topBottom = 0.75.cssRem, leftRight = 1.4.cssRem)
            .borderRadius(999.px)
            .border(1.px, LineStyle.Solid, SiteColors.ink)
            .color(SiteColors.ink)
            .textDecorationLine(TextDecorationLine.None)
            .transition(Transition.of("background-color", 200.ms), Transition.of("color", 200.ms))
    }
    hover { Modifier.backgroundColor(SiteColors.ink).color(SiteColors.paper) }
}
