package dev.theseuntaylor.site.pages

import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.silk.style.toAttrs
import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.toModifier
import dev.theseuntaylor.arcade.Arcade
import dev.theseuntaylor.site.*
import dev.theseuntaylor.site.components.layouts.PageLayoutData
import dev.theseuntaylor.site.components.widgets.Reveal
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Iframe
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLIFrameElement

/** Where the Compose Wasm build is copied to (see site/build.gradle.kts). */
private const val ArcadeAppPath = "/play/index.html"

@InitRoute
fun initArcadePage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Arcade"))
}

val CabinetLayoutStyle = CssStyle {
    base { Modifier.display(DisplayStyle.Grid).gap(2.cssRem).styleModifier { property("grid-template-columns", "1fr") } }
    Breakpoint.MD { Modifier.styleModifier { property("grid-template-columns", "16rem 1fr") } }
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun ArcadePage() {
    var selected by remember { mutableStateOf<String?>(null) }
    var cabinet by remember { mutableStateOf<HTMLIFrameElement?>(null) }
    // Route the running Wasm app by poking its #fragment directly. Changing the iframe's src instead can make the
    // browser reload the whole app (and re-download ~10 MB of Wasm).
    LaunchedEffect(selected, cabinet) {
        val id = selected ?: return@LaunchedEffect
        cabinet?.contentWindow?.location?.hash = id
    }

    Div(ContainerStyle.toModifier().padding { top(9.cssRem); bottom(6.cssRem) }.toAttrs()) {
        Reveal { H1(DisplayHeadingStyle.toAttrs()) { Text("arcade") } }
        Reveal(delayMs = 120) {
            P(LeadStyle.toAttrs()) {
                Text("Games from my apps, running in your browser. The logic is shared Kotlin; the screen below is " +
                    "Compose Multiplatform compiled to WebAssembly.")
            }
        }

        Reveal(Modifier.margin { top(3.cssRem) }, delayMs = 240) {
            Div(CabinetLayoutStyle.toAttrs()) {
                Column(Modifier.borderTop(1.px, LineStyle.Solid, SiteColors.hairline)) {
                    Arcade.games.forEach { game ->
                        Row(
                            ListRowStyle.toModifier()
                                .cursor(Cursor.Pointer)
                                .gap(0.9.cssRem)
                                .thenIf(selected == game.id) { Modifier.color(SiteColors.accent).padding { left(0.75.cssRem) } }
                                .onClick { selected = game.id }
                                .attrsModifier { attr("data-game", game.id) },
                        ) {
                            SpanText(game.emoji, Modifier.fontSize(1.4.cssRem))
                            Column {
                                SpanText(game.title, SerifTitleStyle.toModifier().fontSize(1.3.cssRem))
                                SpanText(if (game.playable) "playable" else "coming soon", MutedTextStyle.toModifier().fontSize(0.8.cssRem))
                            }
                        }
                    }
                }

                // The cabinet: a dark bezel around the Wasm app.
                Div(
                    Modifier
                        .backgroundColor(SiteColors.night)
                        .padding(0.9.cssRem)
                        .borderRadius(1.25.cssRem)
                        .boxShadow(offsetY = 30.px, blurRadius = 60.px, color = SiteColors.accentFixed.toRgb().copyf(alpha = 0.25f))
                        .toAttrs()
                ) {
                    Iframe(attrs = {
                        attr("src", ArcadeAppPath)
                        ref { element ->
                            cabinet = element
                            onDispose { cabinet = null }
                        }
                        attr("title", "Arcade")
                        attr("loading", "lazy")
                        style {
                            width(100.percent)
                            property("aspect-ratio", "16 / 10")
                            property("border", "0")
                            borderRadius(0.6.cssRem)
                            display(DisplayStyle.Block)
                            backgroundColor(SiteColors.night)
                        }
                    })
                    Row(Modifier.fillMaxWidth().padding { top(0.75.cssRem) }.justifyContent(JustifyContent.SpaceBetween)) {
                        SpanText("● ● ●", Modifier.color(SiteColors.green).fontSize(0.7.cssRem).letterSpacing(0.3.em))
                        Link(ArcadeAppPath, "full screen ↗", NavLinkStyle.toModifier().color(SiteColors.onDark).fontSize(0.8.cssRem).textAlign(TextAlign.End))
                    }
                }
            }
        }
    }
}
