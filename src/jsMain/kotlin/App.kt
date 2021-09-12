package pl.mareklangiewicz.kthreelhu

import org.jetbrains.compose.web.renderComposable
import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import org.jetbrains.compose.common.material.Text
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.ui.Styles

@OptIn(ExperimentalComposeWebWidgetsApi::class)
fun main() {
    val root = document.getElementById("root") as HTMLElement

    renderComposable(root = root) {
        Style(Styles)
        H2 {
            Text("Kthreelhu JS")
        }
    }
}
