package clearmap.ui.views

import javafx.scene.Parent
import javafx.scene.control.TabPane
import tornadofx.View
import tornadofx.hbox
import tornadofx.tab
import tornadofx.tabpane


class ClearMapRootView  : View() {
    override val root: Parent = hbox {
        tabpane {
            prefWidth = 800.0
            tab("Map") {
                tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            }
            tab("Cel Spaces") {
                tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            }
        }
    }

}