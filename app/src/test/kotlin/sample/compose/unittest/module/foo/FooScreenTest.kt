package sample.compose.unittest.module.foo

import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import sample.compose.unittest.App
import sample.compose.unittest.TestActivity
import sample.compose.unittest.module.app.mockInjection
import sample.compose.unittest.provider.MockLocalDataProvider
import sample.compose.unittest.provider.mockContexts
import sample.compose.unittest.setInjection

@RunWith(RobolectricTestRunner::class)
internal class FooScreenTest {
    @get:Rule
    val rule = createAndroidComposeRule<TestActivity>()

    @Test(timeout = 10_000)
    fun initialTextTest() {
        val initialText = "foobar"
        val injection = mockInjection(
            contexts = mockContexts(),
            local = MockLocalDataProvider(foo = initialText),
        )
        App.setInjection(injection)
        rule.setContent {
            FooScreen()
        }
        val isText = hasContentDescription("FooScreen:text")
        rule.waitUntil {
            rule.onAllNodes(isText and hasText(initialText))
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }
}
