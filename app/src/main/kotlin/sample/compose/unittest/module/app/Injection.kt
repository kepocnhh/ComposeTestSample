package sample.compose.unittest.module.app

import sample.compose.unittest.provider.Contexts
import sample.compose.unittest.provider.LocalDataProvider

internal data class Injection(
    val contexts: Contexts,
    val local: LocalDataProvider,
)
