package dev.theseuntaylor.site.components.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.css.TransitionTimingFunction
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.icons.MoonIcon
import com.varabyte.kobweb.silk.components.icons.SunIcon
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import dev.theseuntaylor.site.SiteColors
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button

val ColorModeToggleStyle = CssStyle {
    base {
        Modifier
            .size(2.cssRem)
            .flexShrink(0)
            .display(DisplayStyle.Flex)
            .alignItems(AlignItems.Center)
            .justifyContent(JustifyContent.Center)
            .fontSize(1.05.cssRem)
            .color(SiteColors.ink)
            .backgroundColor(Color.transparent)
            .border(1.px, LineStyle.Solid, SiteColors.hairline)
            .borderRadius(50.percent)
            .cursor(Cursor.Pointer)
            .transition(
                Transition.of("transform", 500.ms, TransitionTimingFunction.cubicBezier(0.2, 0.7, 0.2, 1.0)),
                Transition.of("color", 200.ms),
                Transition.of("border-color", 200.ms),
            )
    }
    Breakpoint.MD { Modifier.size(2.25.cssRem) }
    hover { Modifier.color(SiteColors.accent).border(1.px, LineStyle.Solid, SiteColors.accent).rotate(25.deg) }
}

/** Day/night switch: shows the moon by day (tap for night) and the sun by night (tap for day). */
@Composable
fun ColorModeToggle(modifier: Modifier = Modifier) {
    var colorMode by ColorMode.currentState
    val label = if (colorMode.isLight) "Switch to night mode" else "Switch to day mode"
    Button(
        ColorModeToggleStyle.toModifier()
            .then(modifier)
            .onClick { colorMode = colorMode.opposite }
            .toAttrs {
                attr("aria-label", label)
                attr("title", label)
                attr("data-color-mode-toggle", colorMode.name.lowercase())
            }
    ) {
        if (colorMode.isLight) MoonIcon() else SunIcon()
    }
}
