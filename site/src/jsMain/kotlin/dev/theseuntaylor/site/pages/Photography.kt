package dev.theseuntaylor.site.pages

import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.silk.style.toAttrs
import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.ObjectFit
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
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.toModifier
import dev.theseuntaylor.site.*
import dev.theseuntaylor.site.components.layouts.PageLayoutData
import dev.theseuntaylor.site.components.widgets.PhotoFrame
import dev.theseuntaylor.site.components.widgets.Reveal
import dev.theseuntaylor.site.data.Photo
import dev.theseuntaylor.site.data.photos
import kotlinx.browser.document
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

@InitRoute
fun initPhotographyPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Photography"))
}

/** CSS columns give a masonry layout with no JS: each photo keeps its own ratio. */
val MasonryStyle = CssStyle {
    base { Modifier.styleModifier { property("columns", "1"); property("column-gap", "1.25rem") } }
    Breakpoint.SM { Modifier.styleModifier { property("columns", "2") } }
    Breakpoint.LG { Modifier.styleModifier { property("columns", "3"); property("column-gap", "1.75rem") } }
}

val MasonryItemStyle = CssStyle.base {
    Modifier.margin { bottom(1.75.cssRem) }.styleModifier { property("break-inside", "avoid") }
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun PhotographyPage() {
    var open by remember { mutableStateOf<Int?>(null) }

    Div(ContainerStyle.toModifier().padding { top(9.cssRem); bottom(6.cssRem) }.toAttrs()) {
        Reveal { H1(DisplayHeadingStyle.toAttrs()) { Text("photography") } }
        Reveal(delayMs = 120) {
            P(LeadStyle.toAttrs()) { Text("Light, colour and the occasional stranger. Click any frame to see it larger.") }
        }
        Div(MasonryStyle.toModifier().margin { top(3.cssRem) }.toAttrs()) {
            photos.forEachIndexed { i, photo ->
                Div(MasonryItemStyle.toModifier().attrsModifier { attr("data-photo", i.toString()) }.toAttrs()) {
                    Reveal(delayMs = (i % 3) * 100) {
                        PhotoFrame(photo, onClick = { open = i })
                        Row(Modifier.fillMaxWidth().justifyContent(JustifyContent.SpaceBetween).margin { top(0.6.cssRem) }) {
                            SpanText(photo.title, SerifTitleStyle.toModifier().fontSize(1.05.cssRem))
                            SpanText("${photo.place}, ${photo.year}", MutedTextStyle.toModifier().fontSize(0.8.cssRem))
                        }
                    }
                }
            }
        }
    }

    open?.let { index -> Lightbox(photos, index, onIndexChange = { open = it }, onClose = { open = null }) }
}

@Composable
private fun Lightbox(all: List<Photo>, index: Int, onIndexChange: (Int) -> Unit, onClose: () -> Unit) {
    val photo = all[index]
    val prev = { onIndexChange((index - 1 + all.size) % all.size) }
    val next = { onIndexChange((index + 1) % all.size) }

    DisposableEffect(index) {
        val onKey: (Event) -> Unit = { event ->
            when ((event as KeyboardEvent).key) {
                "Escape" -> onClose()
                "ArrowLeft" -> prev()
                "ArrowRight" -> next()
            }
        }
        document.addEventListener("keydown", onKey)
        document.body?.style?.overflowY = "hidden"
        onDispose {
            document.removeEventListener("keydown", onKey)
            document.body?.style?.overflowY = ""
        }
    }

    Box(
        Modifier
            .position(Position.Fixed)
            .top(0.px).right(0.px).bottom(0.px).left(0.px)
            .zIndex(30)
            .backgroundColor(SiteColors.paper.toRgb().copyf(alpha = 0.97f))
            .padding(1.25.cssRem)
            .onClick { onClose() }
            .attrsModifier { attr("role", "dialog"); attr("aria-label", photo.title) },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.maxWidth(64.cssRem).fillMaxWidth().onClick { it.stopPropagation() },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.fillMaxWidth().maxHeight(78.vh).styleModifier { property("aspect-ratio", "${photo.width} / ${photo.height}") }) {
                if (photo.src != null) {
                    Image(photo.src, photo.title, Modifier.fillMaxSize().objectFit(ObjectFit.Contain))
                } else {
                    PhotoFrame(photo, Modifier.fillMaxSize())
                }
            }
            Row(
                Modifier.fillMaxWidth().margin { top(1.cssRem) }.justifyContent(JustifyContent.SpaceBetween),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SpanText("←", NavLinkStyle.toModifier().fontSize(1.5.cssRem).cursor(Cursor.Pointer).onClick { prev() })
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SpanText(photo.title, SerifTitleStyle.toModifier())
                    SpanText("${photo.place}, ${photo.year}  ·  ${index + 1} / ${all.size}", MutedTextStyle.toModifier().fontSize(0.85.cssRem))
                }
                SpanText("→", NavLinkStyle.toModifier().fontSize(1.5.cssRem).cursor(Cursor.Pointer).onClick { next() })
            }
        }
        SpanText(
            "close ✕",
            NavLinkStyle.toModifier().position(Position.Absolute).top(1.5.cssRem).right(1.5.cssRem).cursor(Cursor.Pointer).onClick { onClose() },
        )
    }
}
