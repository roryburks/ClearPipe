package clearpipe.ui

import javafx.collections.FXCollections
import tornadofx.*


class AnimListView : View() {
    val controller : AnimListController by inject()

    override val root = vbox {
        listview(controller.values)
        hbox {
            button("1") {action { controller.values.add("3") }}
            button("2") {action { controller.values.clear() }}
        }
    }
}

class AnimListController : Controller() {
    val values = FXCollections.observableArrayList("1","2")
}