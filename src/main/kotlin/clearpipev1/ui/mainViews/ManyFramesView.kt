package clearpipev1.ui.mainViews

import clearpipev1.model.animation.AafAnimationK
import clearpipev1.model.master.IMasterControl
import clearpipev1.ui.dataFormats.Formats
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

    private val aafsLoaded = mutableMapOf<AafAnimationK, FrameOverlayView>()

    init /*Drag and Drop */ {
        root.setOnDragOver { evt ->
            if( evt.dragboard.hasContent(Formats.internalFormat) && master.dragManager.drag?.supports(Formats.aafAnimFormat) == true) {
                evt.acceptTransferModes(TransferMode.COPY)
                evt.consume()
            }
        }
        root.setOnDragDropped { evt ->
            val aaf = master.dragManager.drag?.get(Formats.aafAnimFormat) as? AafAnimationK ?: return@setOnDragDropped

            if( !aafsLoaded.containsKey(aaf)) {
                val frameView = FrameOverlayView(master, aaf)
                aafsLoaded[aaf] = frameView
                root.add(frameView)
            }
        }
    }
}