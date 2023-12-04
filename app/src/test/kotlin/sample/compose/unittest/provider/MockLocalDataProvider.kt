package sample.compose.unittest.provider

internal class MockLocalDataProvider(
    override var foo: String = "",
    override var bar: String = "",
) : LocalDataProvider
