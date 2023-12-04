package sample.compose.unittest.module.foo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import sample.compose.unittest.module.app.Injection
import sample.compose.unittest.util.AbstractViewModel

internal class FooViewModel(private val injection: Injection) : AbstractViewModel() {
    private val _text = MutableStateFlow<String?>(null)
    val text = _text.asStateFlow()

    fun requestText() {
        injection.launch {
            _text.value = withContext(injection.contexts.default) {
                injection.local.foo
            }
        }
    }

    fun updateText(value: String) {
        injection.launch {
            withContext(injection.contexts.default) {
                injection.local.foo = value
            }
            _text.value = value
        }
    }
}
