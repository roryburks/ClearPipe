package clearpipe.ui.mainViews

import clearpipe.model.imageData.AafAnimation
import clearpipe.model.master.IMasterControl
import clearpipe.ui.dataFormats.Formats
import javafx.scene.Parent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*

class ManyFramesView(val master: IMasterControl) : View() {
    override val root = flowpane {
        vgrow = Priority.ALWAYS
        hgrow = Priority.NEVER
        style { backgroundColor += Color.YELLOWGREEN }

    }

    private val aafsLoaded = mutableMapOf<AafAnimation, FrameOverlayView>()

    init /*Drag and Drop */ {
        root.setOnDragOver { evt ->
            if( evt.dragboard.hasContent(Formats.internalFormat) && master.dragManager.drag?.supports(Formats.aafAnimFormat) == true) {
                evt.acceptTransferModes(TransferMode.COPY)
                evt.consume()
            }
        }
        root.setOnDragDropped { evt ->
            val aaf = master.dragManager.drag?.get(Formats.aafAnimFormat) as? AafAnimation ?: return@setOnDragDropped

            if( !aafsLoaded.containsKey(aaf)) {
                val frameView = FrameOverlayView(master, aaf)
                aafsLoaded[aaf] = frameView
                root.add(frameView)
            }
        }
    }
}