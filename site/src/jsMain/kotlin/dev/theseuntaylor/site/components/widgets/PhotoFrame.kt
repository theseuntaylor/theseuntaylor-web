package dev.theseuntaylor.site.components.widgets

import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.css.TransitionTimingFunction
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.css.Overflow
import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.ObjectFit
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.css.PointerEvents
import org.jetbrains.compose.web.dom.Video
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.toModifier
import dev.theseuntaylor.site.EyebrowStyle
import dev.theseuntaylor.site.data.Photo
import org.jetbrains.compose.web.css.*

val PhotoFrameStyle = CssStyle {
    base {
        Modifier
            .fillMaxWidth()
            .position(Position.Relative)
            .overflow(Overflow.Hidden)
            .borderRadius(2.px)
            .cursor(Cursor.ZoomIn)
    }
    cssRule(" > *") { Modifier.transition(Transition.of("transform", 900.ms, TransitionTimingFunction.cubicBezier(0.2, 0.7, 0.2, 1.0))) }
    cssRule(":hover > *") { Modifier.scale(1.04) }
}

val VideoBadgeStyle = CssStyle.base {
    Modifier
        .position(Position.Absolute)
        .bottom(0.75.cssRem)
        .left(0.75.cssRem)
        .padding(topBottom = 0.2.cssRem, leftRight = 0.6.cssRem)
        .borderRadius(999.px)
        .fontSize(0.7.cssRem)
        .letterSpacing(0.12.em)
        .color(Colors.White)
        .backgroundColor(Color.rgba(0, 0, 0, 0.45f))
        .pointerEvents(PointerEvents.None)
}

private val inViewObserver: dynamic by lazy {
    js(
        """new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                var video = entry.target;
                if (entry.isIntersecting) { video.play().catch(function () {}); } else { video.pause(); }
            });
        }, { threshold: 0.5 })"""
    )
}

/**
 * A muted, looping clip that only plays while at least half of it is on screen. `preload="none"` plus the poster
 * means nothing is downloaded until the visitor actually scrolls to it.
 */
@Composable
private fun InViewVideo(src: String, poster: String?, label: String) {
    Video(attrs = {
        attr("src", src)
        poster?.let { attr("poster", it) }
        attr("aria-label", label)
        attr("preload", "none")
        attr("muted", "")
        attr("loop", "")
        attr("playsinline", "")
        attr("data-photo-video", "")
        style {
            width(100.percent)
            height(100.percent)
            property("object-fit", "cover")
            display(DisplayStyle.Block)
        }
        ref { element ->
            element.asDynamic().muted = true // the attribute alone isn't enough for autoplay rules in every browser
            inViewObserver.observe(element)
            onDispose { inViewObserver.unobserve(element) }
        }
    })
}

/** A photo at its natural ratio, or a tinted placeholder frame when [Photo.src] is still null. */
@Composable
fun PhotoFrame(photo: Photo, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    Box(
        PhotoFrameStyle.toModifier()
            .styleModifier { property("aspect-ratio", "${photo.width} / ${photo.height}") }
            .thenIf(onClick != null) { Modifier.onClick { onClick?.invoke() } }
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (photo.video != null) {
            InViewVideo(photo.video, poster = photo.src, label = photo.title)
            SpanText("▶ video", VideoBadgeStyle.toModifier())
        } else if (photo.src != null) {
            Image(photo.src, photo.title, Modifier.fillMaxSize().objectFit(ObjectFit.Cover).display(DisplayStyle.Block))
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .styleModifier { property("background", "linear-gradient(160deg, ${photo.tint}, #F4F0EC)") },
                contentAlignment = Alignment.Center,
            ) {
                // The tints are light in both modes, so the label keeps day ink rather than following the theme.
                SpanText(
                    photo.title,
                    EyebrowStyle.toModifier().color(Color.rgba(38, 38, 38, 0.62f)).attrsModifier { attr("data-photo-label", "") },
                )
            }
        }
    }
}
