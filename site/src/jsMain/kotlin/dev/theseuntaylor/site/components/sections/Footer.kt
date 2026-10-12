package dev.theseuntaylor.site.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import dev.theseuntaylor.site.MutedTextStyle
import dev.theseuntaylor.site.NavLinkStyle
import dev.theseuntaylor.site.SiteColors
import dev.theseuntaylor.site.data.Me
import org.jetbrains.compose.web.css.*
import kotlin.js.Date

val FooterStyle = CssStyle {
    base { Modifier.padding(topBottom = 1.75.cssRem, leftRight = 1.25.cssRem) }
    Breakpoint.MD { Modifier.padding(topBottom = 1.75.cssRem, leftRight = 4.5.cssRem) }
}

@Composable
fun Footer(modifier: Modifier = Modifier) {
    Row(
        FooterStyle.toModifier()
            .then(modifier)
            .fillMaxWidth()
            .borderTop(1.px, LineStyle.Solid, SiteColors.hairline)
            .flexWrap(FlexWrap.Wrap)
            .gap(1.cssRem)
            .justifyContent(JustifyContent.SpaceBetween),
    ) {
        SpanText("© ${Date().getFullYear()} ${Me.name}", MutedTextStyle.toModifier())
        Row(Modifier.gap(0.4.cssRem)) {
            SpanText("Written in Kotlin with", MutedTextStyle.toModifier())
            Link("https://kobweb.varabyte.com", "Kobweb", NavLinkStyle.toModifier())
            SpanText("&", MutedTextStyle.toModifier())
            Link("https://www.jetbrains.com/compose-multiplatform/", "Compose Multiplatform", NavLinkStyle.toModifier())
        }
    }
}
