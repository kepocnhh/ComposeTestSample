package sample.compose.unittest.module.foo

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import sample.compose.unittest.App
import sample.compose.unittest.TestActivity
import sample.compose.unittest.clearStores
import sample.compose.unittest.module.app.mockInjection
import sample.compose.unittest.provider.MockLocalDataProvider
import sample.compose.unittest.setInjection

@RunWith(RobolectricTestRunner::class)
internal class FooScreenTest {
    @get:Rule
    val rule = createAndroidComposeRule<TestActivity>()

    @Before
    fun before() {
        App.clearStores()
    }

    @Test(timeout = 10_000)
    fun clearTest() {
        val initialText = "foobar:foo"
        val injection = mockInjection(
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
                .size == 1
        }
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        val isClear = hasContentDescription("FooScreen:clear:text")
        rule.onNode(isButton and isClear).performClick()
        rule.waitUntil {
            rule.onAllNodes(isText and hasText(""))
                .fetchSemanticsNodes()
                .size == 1
        }
    }

    @Test(timeout = 10_000)
    fun initialTextTest() {
        val initialText = "foobar:foo"
        val injection = mockInjection(
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
                .size == 1
        }
    }

    @Test(timeout = 10_000)
    fun setTest() {
        val initialText = "foobar:foo"
        val injection = mockInjection(
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
                .size == 1
        }
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        val isClear = hasContentDescription("FooScreen:set:text")
        rule.onNode(isButton and isClear).performClick()
        rule.waitUntil {
            rule.onAllNodes(isText and hasText("foo"))
                .fetchSemanticsNodes()
                .size == 1
        }
    }
}
