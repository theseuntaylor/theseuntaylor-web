package dev.theseuntaylor.site.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs
import org.jetbrains.compose.web.dom.Div

private val revealObserver: dynamic by lazy {
    js(
        """new IntersectionObserver(function (entries, observer) {
            entries.forEach(function (entry) {
                if (entry.isIntersecting) { entry.target.classList.add('in'); observer.unobserve(entry.target); }
            });
        }, { threshold: 0.12, rootMargin: '0px 0px -40px 0px' })"""
    )
}

/** Fades and lifts [content] into place the first time it scrolls into view. [delayMs] staggers siblings. */
@Composable
fun Reveal(modifier: Modifier = Modifier, delayMs: Int = 0, content: @Composable () -> Unit) {
    Div(modifier.toAttrs {
        classes("reveal")
        if (delayMs > 0) style { property("transition-delay", "${delayMs}ms") }
        ref { element ->
            revealObserver.observe(element)
            onDispose { revealObserver.unobserve(element) }
        }
    }) {
        content()
    }
}
