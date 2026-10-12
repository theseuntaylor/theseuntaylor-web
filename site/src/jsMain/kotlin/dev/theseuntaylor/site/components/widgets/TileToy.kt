package dev.theseuntaylor.site.components.widgets

import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.css.TransitionTimingFunction
import com.varabyte.kobweb.compose.ui.thenIf
import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.theseuntaylor.site.SiteColors
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import kotlin.random.Random

private const val Side = 4

val TileStyle = CssStyle.base {
    Modifier
        .borderRadius(0.6.cssRem)
        .cursor(Cursor.Pointer)
        .styleModifier { property("aspect-ratio", "1") }
        .transition(
            Transition.of("background-color", 450.ms, TransitionTimingFunction.EaseOut),
            Transition.of("transform", 300.ms, TransitionTimingFunction.EaseOut),
        )
}

/**
 * A tiny nod to StackBoxes: tiles light up on their own, and light up under the cursor too.
 * Click one and it stays lit until it's clicked again.
 */
@Composable
fun TileToy(modifier: Modifier = Modifier) {
    var ambient by remember { mutableStateOf(setOf<Int>()) }
    var pinned by remember { mutableStateOf(setOf<Int>()) }
    var hovered by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            val tile = Random.nextInt(Side * Side)
            ambient = ambient + tile
            delay(650)
            ambient = ambient - tile
            delay(150)
        }
    }

    Div(modifier.toAttrs {
        style {
            display(DisplayStyle.Grid)
            property("grid-template-columns", "repeat($Side, 1fr)")
            gap(0.6.cssRem)
        }
    }) {
        repeat(Side * Side) { index ->
            val lit = index in ambient || index in pinned || index == hovered
            val color = when {
                index in pinned -> SiteColors.accent
                lit -> if (index % 3 == 0) SiteColors.green else SiteColors.accent.toRgb().copyf(alpha = 0.75f)
                else -> SiteColors.hairline
            }
            Div(
                TileStyle.toModifier()
                    .backgroundColor(color)
                    .thenIf(lit) { Modifier.scale(0.92) }
                    .onMouseEnter { hovered = index }
                    .onMouseLeave { if (hovered == index) hovered = null }
                    .onClick { pinned = if (index in pinned) pinned - index else pinned + index }
                    .toAttrs()
            )
        }
    }
}
