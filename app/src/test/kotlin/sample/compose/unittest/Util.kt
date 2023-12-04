package sample.compose.unittest

import sample.compose.unittest.module.app.Injection

internal fun App.Companion.setInjection(injection: Injection) {
    val field = App::class.java.getDeclaredField("_injection")
    field.isAccessible = true
    field.set(this, injection)
}
