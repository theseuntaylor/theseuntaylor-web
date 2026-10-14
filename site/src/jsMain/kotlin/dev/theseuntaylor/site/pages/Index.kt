package dev.theseuntaylor.site.pages

import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.silk.style.toAttrs
import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.FontStyle
import com.varabyte.kobweb.compose.css.PointerEvents
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.icons.fa.FaEnvelope
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.icons.fa.FaGooglePlay
import com.varabyte.kobweb.silk.components.icons.fa.FaLocationDot
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.style.toModifier
import dev.theseuntaylor.arcade.Arcade
import dev.theseuntaylor.site.*
import dev.theseuntaylor.site.components.layouts.PageLayoutData
import dev.theseuntaylor.site.components.widgets.PhotoFrame
import dev.theseuntaylor.site.components.widgets.Reveal
import dev.theseuntaylor.site.components.widgets.rememberLocalTime
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.silk.style.breakpoint.displayUntil
import dev.theseuntaylor.site.components.widgets.TileToy
import dev.theseuntaylor.site.data.Me
import dev.theseuntaylor.site.data.Project
import dev.theseuntaylor.site.data.featuredProjects
import dev.theseuntaylor.site.data.photos
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import kotlin.js.Date
import kotlin.js.dateLocaleOptions

@InitRoute
fun initHomePage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData(null))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun HomePage() {
    Hero()
    About()
    NameBlock()
    Work()
    ArcadeTeaser()
    PhotographyTeaser()
    Connect()
}

val HeroStyle = CssStyle {
    base {
        Modifier
            .minHeight(100.vh)
            .padding { top(8.cssRem); bottom(4.cssRem) }
            .gap(3.cssRem)
            .flexDirection(FlexDirection.Column)
            .justifyContent(JustifyContent.Center)
    }
    Breakpoint.MD { Modifier.flexDirection(FlexDirection.Row).alignItems(AlignItems.Center).gap(5.cssRem) }
}

val HeroHeadingStyle = CssStyle {
    base {
        Modifier
            .fontFamily(SerifFont, "Georgia", "serif")
            .fontWeight(400)
            .fontSize(3.4.cssRem)
            .lineHeight(1.02)
            .letterSpacing((-0.02).em)
            .margin(0.px, 0.px, 1.75.cssRem, 0.px)
    }
    Breakpoint.MD { Modifier.fontSize(4.75.cssRem) }
    Breakpoint.LG { Modifier.fontSize(6.cssRem) }
}

@Composable
private fun Hero() {
    Row(ContainerStyle.toModifier().then(HeroStyle.toModifier())) {
        Column(Modifier.flex(1)) {
            Reveal { SpanText("${Me.role} · Photographer", EyebrowStyle.toModifier()) }
            Reveal(delayMs = 120) {
                H1(HeroHeadingStyle.toAttrs()) {
                    Text("apps you can ")
                    Span(ItalicStyle.toAttrs()) { Text("tap") }
                    Text(", and pictures worth a ")
                    Span(ItalicStyle.toAttrs()) { Text("pause") }
                    Text(".")
                }
            }
            Reveal(delayMs = 240) {
                Row(Modifier.gap(1.cssRem).flexWrap(FlexWrap.Wrap)) {
                    Link("/#work", "See the work", PillButtonStyle.toModifier())
                    Link("/arcade", "Play the arcade →", NavLinkStyle.toModifier().alignSelf(AlignSelf.Center))
                }
            }
        }
        Reveal(Modifier.width(100.percent).maxWidth(20.cssRem), delayMs = 360) {
            TileToy()
            SpanText("a tiny StackBoxes. hover it, click it.", MutedTextStyle.toModifier().display(DisplayStyle.Block).margin { top(0.9.cssRem) }.fontStyle(FontStyle.Italic))
        }
    }
}

@Composable
private fun SectionHeading(text: String) {
    Reveal { H2(DisplayHeadingStyle.toAttrs()) { Text(text) } }
}

@Composable
private fun About() {
    Div(ContainerStyle.toModifier().then(SectionStyle.toModifier()).id("about").toAttrs()) {
        SectionHeading("about")
        Me.intro.forEachIndexed { i, paragraph ->
            Reveal(delayMs = i * 120) { P(LeadStyle.toAttrs()) { Text(paragraph) } }
        }
        Reveal(Modifier.margin { top(2.5.cssRem) }, delayMs = 240) {
            SpanText("toolbox", EyebrowStyle.toModifier().display(DisplayStyle.Block).margin { bottom(0.6.cssRem) })
            SpanText(Me.toolbox.joinToString("  ·  "), MutedTextStyle.toModifier().fontSize(1.05.cssRem))
        }
    }
}

val NameBlockStyle = CssStyle {
    // Stretch, so both halves are always the same height whatever shape the photo or clip is.
    base { Modifier.flexDirection(FlexDirection.Column).alignItems(AlignItems.Stretch) }
    Breakpoint.MD { Modifier.flexDirection(FlexDirection.Row).height(34.cssRem) }
}

/** Each half takes exactly half, padding included, so the name's padding can't tip the split. */
val NameBlockHalfStyle = CssStyle {
    base { Modifier.styleModifier { property("box-sizing", "border-box") } }
    Breakpoint.MD { Modifier.styleModifier { property("flex", "0 0 50%"); property("min-width", "0") } }
}

/** The two-up block from the editorial layout: a photo on one side, a solid accent panel with the name on the other. */
@Composable
private fun NameBlock() {
    Reveal(FullBleedStyle.toModifier()) {
        Row(NameBlockStyle.toModifier()) {
            // The media fills this half rather than sizing it, so a 16:9 clip can't make it taller than the panel.
            Box(NameBlockHalfStyle.toModifier().minHeight(20.cssRem).position(Position.Relative).overflow(Overflow.Hidden)) {
                PhotoFrame(
                    photos.first(),
                    Modifier.position(Position.Absolute).top(0.px).right(0.px).bottom(0.px).left(0.px)
                        .styleModifier { property("aspect-ratio", "auto") }
                        .attrsModifier { attr("data-name-block-media", "") },
                )
            }
            Column(
                NameBlockHalfStyle.toModifier().minHeight(20.cssRem).backgroundColor(SiteColors.accentFixed).padding(2.cssRem)
                    .attrsModifier { attr("data-name-block-panel", "") },
                verticalArrangement = com.varabyte.kobweb.compose.foundation.layout.Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                H3(
                    DisplayHeadingStyle.toModifier()
                        .color(SiteColors.onDark)
                        .margin(0.px)
                        .textAlign(TextAlign.Center)
                        // Sized to the half-width panel so "George-Taylor" fits on one line.
                        .styleModifier { property("font-size", "clamp(2.25rem, 5.2vw, 5.5rem)") }
                        .toAttrs()
                ) { Text(Me.name.replace('-', '\u2011')) } // non-breaking hyphen keeps the surname together
                SpanText(Me.role, EyebrowStyle.toModifier().color(SiteColors.onDark).margin { top(1.cssRem) })
            }
        }
    }
}

@Composable
private fun Work() {
    var hovered by remember { mutableStateOf<Project?>(null) }
    var cursor by remember { mutableStateOf(0.0 to 0.0) }

    Div(
        ContainerStyle.toModifier()
            .then(SectionStyle.toModifier())
            .id("work")
            .onMouseMove { cursor = it.clientX.toDouble() to it.clientY.toDouble() }
            .toAttrs()
    ) {
        SectionHeading("work")
        Div(Modifier.borderTop(1.px, LineStyle.Solid, SiteColors.hairline).toAttrs()) {
            featuredProjects.forEachIndexed { i, project ->
                Reveal(delayMs = i * 80) {
                    Link(
                        project.href,
                        ListRowStyle.toModifier()
                            .display(DisplayStyle.Flex)
                            .flexWrap(FlexWrap.Wrap)
                            .gap(1.cssRem)
                            .justifyContent(JustifyContent.SpaceBetween)
                            .onMouseEnter { hovered = project }
                            .onMouseLeave { if (hovered == project) hovered = null },
                    ) {
                        Column(Modifier.flex(1).minWidth(16.cssRem)) {
                            H3(SerifTitleStyle.toAttrs()) { Text(project.title) }
                            SpanText(project.summary, MutedTextStyle.toModifier().margin { top(0.35.cssRem) })
                        }
                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.textAlign(TextAlign.End)) {
                            SpanText(project.stack.joinToString(" · "), MutedTextStyle.toModifier().fontSize(0.85.cssRem))
                            project.badge?.let {
                                SpanText(it, EyebrowStyle.toModifier().color(SiteColors.accent).margin { top(0.35.cssRem) })
                            }
                        }
                    }
                }
            }
        }
        Reveal(Modifier.margin { top(2.cssRem) }) {
            Link(Me.github + "?tab=repositories", "Everything else on GitHub →", NavLinkStyle.toModifier())
        }
    }

    // A card that trails the cursor while a project row is hovered (desktop only).
    hovered?.let { project ->
        Column(
            Modifier
                .position(Position.Fixed)
                .left(cursor.first.px)
                .top(cursor.second.px)
                .translate(24.px, (-50).percent)
                .zIndex(20)
                .pointerEvents(PointerEvents.None)
                .width(13.cssRem)
                .padding(1.25.cssRem)
                .backgroundColor(SiteColors.ink)
                .color(SiteColors.paper)
                .borderRadius(0.4.cssRem)
                .displayIfAtLeast(Breakpoint.MD),
        ) {
            SpanText(project.emoji, Modifier.fontSize(2.4.cssRem))
            SpanText(project.title, SerifTitleStyle.toModifier().margin { top(0.5.cssRem) })
            SpanText("open on GitHub ↗", Modifier.fontSize(0.8.cssRem).opacity(0.7))
        }
    }
}

@Composable
private fun ArcadeTeaser() {
    Div(FullBleedStyle.toModifier().backgroundColor(SiteColors.night).color(SiteColors.onDark).toAttrs()) {
        Div(ContainerStyle.toModifier().then(SectionStyle.toModifier()).toAttrs()) {
            Reveal { H2(DisplayHeadingStyle.toModifier().color(SiteColors.green).toAttrs()) { Text("arcade") } }
            Reveal(delayMs = 120) {
                P(LeadStyle.toModifier().color(SiteColors.onDark).toAttrs()) {
                    Text("Some of my apps are games. Their logic is written once in Kotlin Multiplatform and runs here too, " +
                        "compiled to WebAssembly and drawn with Compose. Same code, new screen.")
                }
            }
            Reveal(delayMs = 240) {
                Row(Modifier.gap(1.cssRem).flexWrap(FlexWrap.Wrap).margin { bottom(2.cssRem) }) {
                    Arcade.games.forEach { game ->
                        SpanText(
                            "${game.emoji}  ${game.title}" + if (game.playable) "" else "  · soon",
                            Modifier
                                .fontFamily(MonoFont, "monospace")
                                .fontSize(0.85.cssRem)
                                .padding(topBottom = 0.5.cssRem, leftRight = 1.cssRem)
                                .border(1.px, LineStyle.Solid, SiteColors.accent)
                                .borderRadius(999.px),
                        )
                    }
                }
                Link(
                    "/arcade",
                    "Insert coin →",
                    PillButtonStyle.toModifier().color(SiteColors.night).backgroundColor(SiteColors.green).border(1.px, LineStyle.Solid, SiteColors.green),
                )
            }
        }
    }
}

val PhotoStripStyle = CssStyle {
    base { Modifier.display(DisplayStyle.Grid).gap(1.cssRem).styleModifier { property("grid-template-columns", "1fr 1fr") } }
    Breakpoint.MD { Modifier.gap(1.5.cssRem).styleModifier { property("grid-template-columns", "1fr 1fr 1fr") } }
}

@Composable
private fun PhotographyTeaser() {
    Div(ContainerStyle.toModifier().then(SectionStyle.toModifier()).toAttrs()) {
        SectionHeading("photography")
        Div(PhotoStripStyle.toAttrs()) {
            photos.drop(1).take(3).forEachIndexed { i, photo ->
                Reveal(delayMs = i * 120) {
                    Link("/photography", Modifier.textDecorationLine(TextDecorationLine.None)) {
                        PhotoFrame(photo, Modifier.styleModifier { property("aspect-ratio", "4 / 5") })
                    }
                }
            }
        }
        Reveal(Modifier.margin { top(2.cssRem) }) {
            Link("/photography", "See the gallery →", NavLinkStyle.toModifier())
        }
    }
}

@Composable
private fun ConnectRow(icon: @Composable () -> Unit, label: String, href: String? = null) {
    Row(
        Modifier.gap(0.9.cssRem).padding { topBottom(0.6.cssRem) }.attrsModifier { attr("data-connect-row", "") },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Span(Modifier.color(SiteColors.accent).toAttrs()) { icon() }
        if (href != null) Link(href, label, InlineLinkStyle.toModifier()) else SpanText(label)
    }
}

val ConnectGridStyle = CssStyle {
    base { Modifier.display(DisplayStyle.Grid).gap(3.cssRem).styleModifier { property("grid-template-columns", "1fr") } }
    Breakpoint.MD { Modifier.styleModifier { property("grid-template-columns", "1fr 1fr") } }
}

@Composable
private fun Connect() {
    Div(ContainerStyle.toModifier().then(SectionStyle.toModifier()).id("connect").toAttrs()) {
        SectionHeading("connect")
        Reveal {
            Div(ConnectGridStyle.toAttrs()) {
                Column {
                    H3(SerifTitleStyle.toModifier().margin { bottom(1.cssRem) }.toAttrs()) { Text("Get in touch") }
                    ConnectRow({ FaEnvelope() }, Me.email, "mailto:${Me.email}")
                    ConnectRow({ FaGithub() }, Me.githubLabel, Me.github)
                    ConnectRow({ FaGooglePlay() }, Me.googlePlayLabel, Me.googlePlay)
                }
                // On wider screens the location and time live on the right edge (see Nav); phones get them here.
                Me.location?.let { location ->
                    val now = rememberLocalTime(location)
                    Column(Modifier.displayUntil(Breakpoint.MD).attrsModifier { attr("data-connect-clock", "") }) {
                        H3(SerifTitleStyle.toModifier().margin { bottom(1.cssRem) }.toAttrs()) { Text("Location") }
                        ConnectRow({ FaLocationDot() }, "${location.city}  ${location.flag}")
                        SpanText("$now local time", MutedTextStyle.toModifier().margin { top(0.75.cssRem) })
                    }
                }
            }
        }
    }
}
