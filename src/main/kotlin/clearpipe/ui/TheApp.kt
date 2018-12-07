package clearpipe.ui

import tornadofx.*

class TheApp: App(RootView::class) {
}


fun main(args: Array<String>) {
    launch<TheApp>(args)
}