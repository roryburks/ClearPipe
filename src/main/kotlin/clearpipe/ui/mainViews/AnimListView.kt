package clearpipe.ui.mainViews

import clearpipe.model.imageData.AafAnimation
import clearpipe.model.master.IMasterControl
import clearpipe.ui.dataFormats.Formats
import javafx.collections.FXCollections
import javafx.scene.control.ListCell
import javafx.scene.input.ClipboardContent
import javafx.scene.input.KeyCode
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.util.Callback
import rb.owl.jvm.addWeakObserver
import rb.owl.jvm.javafx.bindTo
import tornadofx.*


class AnimListView(master: IMasterControl) : View() {
    val controller : AnimListController = AnimListController(master)

    val listview = listview(controller.values)

    override val root = vbox {
        add(listview)

        listview.vgrow = Priority.ALWAYS
        listview.cellFactory = Callback {AafListCell(master)}

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

class AafListCell(val master: IMasterControl) : ListCell<AafAnimation?>() {
    var aaf : AafAnimation? = null
    override fun updateItem(item: AafAnimation?, empty: Boolean) {
        text = item?.run { "$name [${celset.name}]"}
        aaf = item
        super.updateItem(item, empty)
    }
    init {
        isEditable = true
        setOnDragDetected {evt ->
            val aaf  = aaf ?: return@setOnDragDetected
            val db = startDragAndDrop(TransferMode.COPY)
            val content = ClipboardContent()
            content.putString(aaf.name)
            content[Formats.internalFormat] = 1
            db.setContent(content)
            master.dragManager.startDragging(Formats.aafAnimFormat, aaf)
            evt.consume()
        }

        setOnKeyPressed {
            if( it.code == KeyCode.F2) {

                it.consume()
            }
        }
    }
}

class AnimListController( master: IMasterControl) : Controller() {
    val values = FXCollections.observableArrayList<AafAnimation>().also { it.bindTo(master.obs.animations) }
}