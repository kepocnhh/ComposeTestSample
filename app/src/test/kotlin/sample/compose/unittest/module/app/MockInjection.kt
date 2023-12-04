package sample.compose.unittest.module.app

import sample.compose.unittest.provider.Contexts
import sample.compose.unittest.provider.LocalDataProvider
import sample.compose.unittest.provider.MockLocalDataProvider
import sample.compose.unittest.provider.mockContexts

internal fun mockInjection(
    contexts: Contexts = mockContexts(),
    local: LocalDataProvider = MockLocalDataProvider(),
): Injection {
    return Injection(
        contexts = contexts,
        local = local,
    )
}
