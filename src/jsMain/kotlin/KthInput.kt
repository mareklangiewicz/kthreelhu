package pl.mareklangiewicz.kthreelhu

import androidx.compose.runtime.*
import kotlinx.browser.*
import org.w3c.dom.events.*
import pl.mareklangiewicz.uwidgets.*

/**
 * Tiny keyboard+mouse input layer: keys trigger actions or flip toggles, and mouse moves reach
 * one listener. It replaces the private AreaKim's Kim, which this public repo must not depend on.
 * Gamepad support was dropped with it.
 *
 * One action per key: registering a key twice at the same time is a bug and fails fast.
 */
class KthInput {
  private val triggers = mutableStateMapOf<Char, () -> Unit>()
  private val toggles = mutableStateMapOf<Char, Boolean>()
  private var mouseMove: ((x: Double, y: Double) -> Unit)? = null

  /** All registered keys, sorted, with their toggle state (null for plain triggers). */
  val keys: List<Pair<Char, Boolean?>> by derivedStateOf {
    (triggers.keys.map { it to null } + toggles.map { (k, v) -> k to v }).sortedBy { it.first }
  }

  fun onKeyDown(key: Char): Boolean {
    triggers[key]?.let { it(); return true }
    toggles[key]?.let { toggles[key] = !it; return true }
    return false
  }

  fun onMouseMove(x: Double, y: Double): Boolean = mouseMove?.let { it(x, y); true } ?: false

  @Composable internal fun InstallTrigger(key: Char, action: () -> Unit) {
    val currentAction by rememberUpdatedState(action)
    DisposableEffect(key) {
      check(key !in triggers && key !in toggles) { "Key '$key' already registered." }
      triggers[key] = { currentAction() }
      onDispose { triggers.remove(key) }
    }
  }

  @Composable internal fun InstallToggle(key: Char, init: Boolean): State<Boolean> {
    DisposableEffect(key) {
      check(key !in triggers && key !in toggles) { "Key '$key' already registered." }
      toggles[key] = init
      onDispose { toggles.remove(key) }
    }
    return remember(key) { derivedStateOf { toggles[key] ?: init } }
  }

  @Composable internal fun InstallMouseMove(action: (x: Double, y: Double) -> Unit) {
    val currentAction by rememberUpdatedState(action)
    DisposableEffect(Unit) {
      check(mouseMove == null) { "Mouse move listener already registered." }
      mouseMove = { x, y -> currentAction(x, y) }
      onDispose { mouseMove = null }
    }
  }

  companion object {
    val Local = staticCompositionLocalOf<KthInput> { error("KthInputArea not provided") }
  }
}

/** Provides a [KthInput] to [content], fed by keydown and mousemove events of [target]. */
@Composable fun KthInputArea(target: EventTarget = window, content: @Composable () -> Unit) {
  val input = remember { KthInput() }
  DomEventEffect("keydown", target) {
    val key = (it as KeyboardEvent).key.singleOrNull() ?: return@DomEventEffect false
    input.onKeyDown(key)
  }
  DomEventEffect("mousemove", target) {
    it as MouseEvent
    input.onMouseMove(it.x, it.y)
  }
  CompositionLocalProvider(KthInput.Local provides input, content = content)
}

@Composable infix fun Char.trigger(action: () -> Unit) = KthInput.Local.current.InstallTrigger(this, action)

@Composable fun Char.toggle(init: Boolean = false): State<Boolean> = KthInput.Local.current.InstallToggle(this, init)

@Composable fun onMouseMove(action: (x: Double, y: Double) -> Unit) = KthInput.Local.current.InstallMouseMove(action)

/** One row listing every registered key; toggled-on keys are bold. */
@Composable fun KthInputBar() {
  val input = KthInput.Local.current
  URow {
    for ((key, toggled) in input.keys) UText(key.toString(), center = true, bold = toggled == true, mono = true)
  }
}

/** Handled events (handler returned true) stop propagating, so outer listeners don't see them. */
@Composable private fun DomEventEffect(type: String, target: EventTarget, handle: (Event) -> Boolean) {
  val currentHandle by rememberUpdatedState(handle)
  DisposableEffect(type, target) {
    val listener: (Event) -> Unit = { if (currentHandle(it)) it.stopPropagation() }
    target.addEventListener(type, listener, true)
    onDispose { target.removeEventListener(type, listener, true) }
  }
}
