package dev.theseuntaylor.arcade.web

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.theseuntaylor.arcade.Arcade
import dev.theseuntaylor.arcade.ArcadeGame
import kotlinx.browser.window
import org.w3c.dom.events.Event

private val Night = Color(0xFF0B0816)
private val Green = Color(0xFF3DDC84)
private val Purple = Color(0xFF7F52FF)

/** The current `#fragment`, kept in sync with the browser so the Kobweb lobby can deep-link straight to a game. */
@Composable
private fun rememberHashRoute(): Pair<String, (String) -> Unit> {
    var hash by remember { mutableStateOf(window.location.hash.removePrefix("#")) }
    DisposableEffect(Unit) {
        val listener: (Event) -> Unit = { hash = window.location.hash.removePrefix("#") }
        window.addEventListener("hashchange", listener)
        onDispose { window.removeEventListener("hashchange", listener) }
    }
    return hash to { window.location.hash = it }
}

@Composable
fun ArcadeApp() {
    val (route, navigate) = rememberHashRoute()
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Night, Color(0xFF1A1035)))),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(Arcade.find(route), transitionSpec = { fadeIn() togetherWith fadeOut() }) { game ->
            if (game == null) GamePicker(onPick = { navigate(it.id) }) else GameScreen(game, onExit = { navigate("") })
        }
    }
}

@Composable
private fun GamePicker(onPick: (ArcadeGame) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
        ArcadeText("SELECT A GAME", Green, 28)
        Spacer(Modifier.height(32.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Arcade.games.forEach { game ->
                Column(
                    Modifier
                        .width(220.dp)
                        .border(2.dp, Purple, RoundedCornerShape(16.dp))
                        .background(Color(0x337F52FF), RoundedCornerShape(16.dp))
                        .clickable { onPick(game) }
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(game.emoji, fontSize = 40.sp)
                    Spacer(Modifier.height(12.dp))
                    ArcadeText(game.title.uppercase(), Color.White, 18)
                    Spacer(Modifier.height(8.dp))
                    Text(game.tagline, color = Color(0xCCFFFFFF), fontSize = 13.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

/** Placeholder cabinet shown until a game's shared logic lands in :arcade:shared. */
@Composable
private fun GameScreen(game: ArcadeGame, onExit: () -> Unit) {
    val blink by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
        Text(game.emoji, fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        ArcadeText(game.title.uppercase(), Color.White, 32)
        Spacer(Modifier.height(24.dp))
        ArcadeText(if (game.playable) "PRESS START" else "COMING SOON", Green, 20, Modifier.alpha(blink))
        Spacer(Modifier.height(40.dp))
        ArcadeText("← BACK", Purple, 16, Modifier.clickable(onClick = onExit).padding(8.dp))
    }
}

@Composable
private fun ArcadeText(text: String, color: Color, size: Int, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier,
        color = color,
        fontSize = size.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        letterSpacing = 3.sp,
        textAlign = TextAlign.Center,
    )
}
