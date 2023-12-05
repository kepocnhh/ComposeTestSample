package sample.compose.unittest.module.qux

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
internal class QuxScreenTest {
    @get:Rule
    val rule = createAndroidComposeRule<TestActivity>()

    @Before
    fun before() {
        App.clearStores()
    }

    @Test(timeout = 10_000)
    fun clearTest() {
        val initialText = "foobar:qux"
        val injection = mockInjection(
            local = MockLocalDataProvider(qux = initialText),
        )
        App.setInjection(injection)
        rule.setContent {
            QuxScreen()
        }
        val isText = hasContentDescription("QuxScreen:text")
        rule.waitUntil {
            rule.onAllNodes(isText and hasText(initialText))
                .fetchSemanticsNodes()
                .size == 1
        }
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        val isClear = hasContentDescription("QuxScreen:clear:text")
        rule.onNode(isButton and isClear).performClick()
        rule.waitUntil {
            rule.onAllNodes(isText and hasText(""))
                .fetchSemanticsNodes()
                .size == 1
        }
    }

    @Test(timeout = 10_000)
    fun initialTextTest() {
        val initialText = "foobar:qux"
        val injection = mockInjection(
            local = MockLocalDataProvider(qux = initialText),
        )
        App.setInjection(injection)
        rule.setContent {
            QuxScreen()
        }
        val isText = hasContentDescription("QuxScreen:text")
        rule.waitUntil {
            rule.onAllNodes(isText and hasText(initialText))
                .fetchSemanticsNodes()
                .size == 1
        }
    }

    @Test(timeout = 10_000)
    fun setTest() {
        val initialText = "foobar:qux"
        val injection = mockInjection(
            local = MockLocalDataProvider(qux = initialText),
        )
        App.setInjection(injection)
        rule.setContent {
            QuxScreen()
        }
        val isText = hasContentDescription("QuxScreen:text")
        rule.waitUntil {
            rule.onAllNodes(isText and hasText(initialText))
                .fetchSemanticsNodes()
                .size == 1
        }
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        val isClear = hasContentDescription("QuxScreen:set:text")
        rule.onNode(isButton and isClear).performClick()
        rule.waitUntil {
            rule.onAllNodes(isText and hasText("qux"))
                .fetchSemanticsNodes()
                .size == 1
        }
    }
}
