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
            .overflow(Overflow.Hidden)
            .borderRadius(2.px)
            .cursor(Cursor.ZoomIn)
    }
    cssRule(" > *") { Modifier.transition(Transition.of("transform", 900.ms, TransitionTimingFunction.cubicBezier(0.2, 0.7, 0.2, 1.0))) }
    cssRule(":hover > *") { Modifier.scale(1.04) }
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
        if (photo.src != null) {
            Image(photo.src, photo.title, Modifier.fillMaxSize().objectFit(ObjectFit.Cover).display(DisplayStyle.Block))
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .styleModifier { property("background", "linear-gradient(160deg, ${photo.tint}, #F4F0EC)") },
                contentAlignment = Alignment.Center,
            ) {
                SpanText(photo.title, EyebrowStyle.toModifier())
            }
        }
    }
}
