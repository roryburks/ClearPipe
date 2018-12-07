package clearpipe.ui

import clearpipe.model.imageData.AafAnimation
import clearpipe.model.master.IMasterControl
import clearpipe.model.master.MasterControl
import javafx.collections.FXCollections
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import rb.owl.jvm.addWeakObserver
import rb.owl.jvm.javafx.bindTo
import tornadofx.*


class AnimListView(master: IMasterControl) : View() {
    val controller : AnimListController = AnimListController(master)

    val listview = listview(controller.values)
    override val root = vbox {
        add(listview)
        listview.vgrow = Priority.ALWAYS

        hbox {
            button("1") {}
            button("2") {}
            vgrow = Priority.NEVER
        }
        style { backgroundColor += Color.RED }
        vgrow = Priority.ALWAYS
    }

    private val wl = master.obs.currentAnimation
        .addWeakObserver { new, old ->listview.selectionModel.select(new)}
    init {
        listview.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            master.projectSet.current?.currentAnimation  = newValue
        }
    }
}

class AnimListController( master: IMasterControl) : Controller() {
    val values = FXCollections.observableArrayList<AafAnimation>().also { it.bindTo(master.obs.animations) }
}