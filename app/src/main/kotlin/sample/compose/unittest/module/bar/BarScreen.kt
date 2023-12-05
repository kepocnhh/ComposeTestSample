package sample.compose.unittest.module.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import sample.compose.unittest.App

@Composable
internal fun BarScreen() {
    val viewModel = App.viewModel<BarViewModel>()
    val text = viewModel.text.collectAsState().value
    LaunchedEffect(Unit) {
        if (text == null) {
            viewModel.requestText()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
        ) {
            BasicText(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp)
                    .wrapContentHeight(),
                text = "bar",
                style = TextStyle(color = Color.Black),
            )
            BasicText(
                modifier = Modifier
                    .semantics {
                        contentDescription = "BarScreen:text"
                    }
                    .fillMaxWidth()
                    .height(64.dp)
                    .wrapContentSize(),
                text = text.orEmpty(),
                style = TextStyle(color = Color.Black),
            )
            BasicText(
                modifier = Modifier
                    .semantics {
                        role = Role.Button
                        contentDescription = "BarScreen:clear:text"
                    }
                    .fillMaxWidth()
                    .height(64.dp)
                    .clickable {
                        viewModel.updateText("")
                    }
                    .wrapContentSize(),
                text = "clear",
                style = TextStyle(color = Color.Black),
            )
            BasicText(
                modifier = Modifier
                    .semantics {
                        role = Role.Button
                        contentDescription = "BarScreen:set:text"
                    }
                    .fillMaxWidth()
                    .height(64.dp)
                    .clickable {
                        viewModel.updateText("bar")
                    }
                    .wrapContentSize(),
                text = "set",
                style = TextStyle(color = Color.Black),
            )
        }
    }
}
