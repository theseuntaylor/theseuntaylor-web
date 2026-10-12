package dev.theseuntaylor.site.components.widgets

import androidx.compose.runtime.*
import dev.theseuntaylor.site.data.Location
import kotlinx.coroutines.delay
import kotlin.js.Date
import kotlin.js.dateLocaleOptions

/** The current HH:MM:SS in [location]'s time zone, ticking on each second boundary. */
@Composable
fun rememberLocalTime(location: Location): String {
    var now by remember(location) { mutableStateOf("") }
    LaunchedEffect(location) {
        while (true) {
            now = Date().toLocaleTimeString("en-GB", dateLocaleOptions {
                hour = "2-digit"; minute = "2-digit"; second = "2-digit"; timeZone = location.timeZone
            })
            delay(1000 - (Date.now() % 1000).toLong())
        }
    }
    return now
}
