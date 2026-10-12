package dev.theseuntaylor.site.components.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.data.getValue
import com.varabyte.kobweb.core.layout.Layout
import dev.theseuntaylor.site.components.sections.Footer
import dev.theseuntaylor.site.components.sections.Nav
import dev.theseuntaylor.site.data.Me
import kotlinx.browser.document
import org.jetbrains.compose.web.css.vh

/** [title] is null for the home page, which uses just the name. */
class PageLayoutData(val title: String?)

@Composable
@Layout
fun PageLayout(ctx: PageContext, content: @Composable ColumnScope.() -> Unit) {
    val data = ctx.data.getValue<PageLayoutData>()
    LaunchedEffect(data.title) {
        document.title = data.title?.let { "$it · ${Me.name}" } ?: "${Me.name} · ${Me.role}"
    }

    Nav(ctx.route.path)
    Column(Modifier.fillMaxWidth().minHeight(100.vh)) {
        Column(Modifier.fillMaxWidth().flexGrow(1)) {
            content()
        }
        Footer()
    }
}
