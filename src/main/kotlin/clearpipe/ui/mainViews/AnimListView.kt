package clearpipe.ui.mainViews

import clearpipe.model.imageData.AafAnimation
import clearpipe.model.master.IMasterControl
import clearpipe.ui.dataFormats.Formats
import javafx.collections.FXCollections
import javafx.scene.control.ListCell
import javafx.scene.input.ClipboardContent
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.util.Callback
import old.rbJvm.jvm.addWeakObserver
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
        setOnMousePressed { evt ->
            if( evt.button == MouseButton.SECONDARY) {
                if( evt.isShiftDown)
                {
                    val curAnim = master.projectSet.current?.currentAnimation
                    if( curAnim != null)
                    {
                        master.projectSet.current?.animationsBind?.list?.remove(curAnim)
                    }
                }
                else {
                    aaf?.also {
                        master.dialog.promptForString("Rename Animation", it.name)?.also { str ->
                            it.name = str
                            // TODO: replace with proper bindings
                            updateItem(it, false)
                        }
                    }
                }
                evt.consume()
            }
        }
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

        setOnKeyPressed { evt ->
            println("x")
            if( evt.code == KeyCode.F2) {

                evt.consume()
            }
        }
    }
}

class AnimListController( master: IMasterControl) : Controller() {
    val values = FXCollections.observableArrayList<AafAnimation>().also { it.bindTo(master.obs.animations) }
}