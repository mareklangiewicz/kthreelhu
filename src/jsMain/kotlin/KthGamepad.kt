package pl.mareklangiewicz.kthreelhu

import org.w3c.dom.*

// Minimal Web Gamepad API declarations (the parts the examples use).
// Browsers differ a lot here, so don't hold on to Gamepad objects: call getGamepads() every frame
// and copy out what you need. See: https://developer.mozilla.org/en-US/docs/Web/API/Gamepad_API

/** Entries are null for empty slots. No copying: this is the browser's own array. */
fun Navigator.getGamepads(): Array<Gamepad?> = asDynamic().getGamepads().unsafeCast<Array<Gamepad?>>()

// https://developer.mozilla.org/en-US/docs/Web/API/Gamepad
external class Gamepad {
  val index: Int
  val id: String
  val mapping: String
  val connected: Boolean
  val buttons: Array<GamepadButton>
  val axes: Array<Double> // -1.0 .. 1.0
  val timestamp: Double? // DOMHighResTimeStamp; nullable because some browsers don't support it
  val vibrationActuator: GamepadHapticActuator?
}

// https://developer.mozilla.org/en-US/docs/Web/API/GamepadButton
external class GamepadButton {
  val value: Double // 0.0 .. 1.0
  val touched: Boolean
  val pressed: Boolean
}

// https://developer.mozilla.org/en-US/docs/Web/API/GamepadHapticActuator
external class GamepadHapticActuator {
  fun playEffect(type: String, parameters: GamepadEffectParameters) // returns a Promise (ignored)
  fun reset() // returns a Promise (ignored)
}

external interface GamepadEffectParameters {
  var duration: Double // ms
  var startDelay: Double // ms
  var strongMagnitude: Double // 0.0 .. 1.0
  var weakMagnitude: Double // 0.0 .. 1.0
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
fun GamepadHapticActuator.playDualRumble(init: GamepadEffectParameters.() -> Unit) =
  playEffect("dual-rumble", (js("{}") as GamepadEffectParameters).apply(init))
