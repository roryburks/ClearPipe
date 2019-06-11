package clearpipe.ui.mainViews.center

import clearpipe.model.master.IMasterControl
import clearpipe.model.imageData.CelSet
import javafx.collections.FXCollections
import javafx.scene.Parent
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import rb.jvm.addWeakObserver
import rb.owl.jvm.javafx.bindTo
import tornadofx.*

class DisplayCelSpaceView(private val master: IMasterControl)
    : View()
{
    val controller = DisplayCelSpaceController(master)

    // region UI objects
    val celSetView = combobox(values =  controller.cellListsFX)
    val draw = DisplayDrawView()
    override val root = vbox {
        add(draw)
        add(celSetView)
        label("tx")
    }
    // endregion

    val trigger = master.obs.currentCel.addWeakObserver { new, old -> celSetView.selectionModel.select(new)}

    init {
        celSetView.setOnAction {
            val selected = celSetView.selectedItem
            master.projectSet.current?.selectedCel = selected
            draw.celSet = selected
        }
    }
}

class DisplayCelSpaceController(private val master: IMasterControl) : Controller() {
    val cellListsFX = FXCollections.observableArrayList<CelSet>().also { it.bindTo(master.obs.celSet) }
}


class DisplayDrawView : View() {
    val canvas = canvas()
    override val root: Parent = scrollpane {
        add(canvas)
        vgrow = Priority.ALWAYS
    }

    var celSet : CelSet? = null
        set(value) {
            if( field != value) {
                field = value
                updatedCelSet()
            }
        }

    private fun updatedCelSet() {
        val celSet = celSet
        canvas.width = celSet?.image?.width ?: 0.0
        canvas.height = celSet?.image?.height ?: 0.0
        draw()
    }
    private fun draw() {
        val gc = canvas.graphicsContext2D
        gc.clearRect(0.0, 0.0, canvas.width, canvas.height)

        val celSet = celSet ?: return
        gc.drawImage(celSet.image, 0.0, 0.0)

        gc.stroke = Color(0.5, 0.5, 0.2, 0.7)
        celSet.cels.forEach {gc.strokeRect(it.x1, it.y1, it.w, it.h)}
    }
}

