package clearmap.ui

import clearmap.ui.views.ClearMapRootView
import tornadofx.App
import tornadofx.launch


class ClearMapApp: App(ClearMapRootView::class) {
}

fun main(args: Array<String>) {
    launch<ClearMapApp>(args)
}