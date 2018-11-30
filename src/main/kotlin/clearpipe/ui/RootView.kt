package clearpipe.ui

import javafx.scene.layout.Priority
import clearpipe.model.Dialog
import clearpipe.model.master.IMasterControl
import clearpipe.model.master.MasterControl
import clearpipe.model.io.AafFileImporter
import tornadofx.*


class RootView  : View() {
    val lbl1 = label("Waiting...")
    val img = canvas()
    val animListView : AnimListView by inject()
    val master: IMasterControl by inject<MasterControl>()

    init {
        (master.dialog as? Dialog)?.stage = primaryStage
    }

    override val root = borderpane {
        top {
            menubar {
                menu("File") {
                    item("Open", "Shortcut+O").action {
                        val toOpen = master.dialog.promptForOpen("Open File") ?: return@action
                        val aaf = AafFileImporter.import(toOpen)
                        val gc = img.graphicsContext2D
                        img.width = aaf.img.width
                        img.height = aaf.img.height
                        gc.drawImage(aaf.img, 0.0, 0.0, aaf.img.width/2, aaf.img.height, 0.0, 0.0, aaf.img.width/2, aaf.img.height)
                    }
                }
            }
        }
        center {
            gridpane {

                add(animListView.root, 0, 0, 1, 2)
                animListView.root.gridpaneConstraints {
                    useMaxWidth = true
                    maxWidth = 200.0
                }

                vbox {
                    //style { backgroundColor +=  Color(1.0, 0.5, 0.5, 1.0) }
                    label("Hello")
                    add(img)
                }.gridpaneConstraints {
                    columnRowIndex(1,0)
                    hGrow = Priority.ALWAYS
                    useMaxWidth = false
                }

                minWidth = 600.0
            }
        }
    }

}
