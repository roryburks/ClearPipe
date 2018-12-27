package clearpipe.ui

import clearpipe.ui.mainViews.AnimListView
import javafx.scene.layout.Priority
import clearpipe.model.Dialog
import clearpipe.model.master.IMasterControl
import clearpipe.model.master.MasterControl
import clearpipe.model.master.Commands.ImportCommand
import clearpipe.model.master.Commands.OpenCommand
import clearpipe.ui.mainViews.ManyFramesView
import clearpipe.ui.mainViews.center.AnimDisplayView
import clearpipe.ui.mainViews.center.DisplayCelSpaceView
import javafx.scene.control.TabPane
import javafx.scene.paint.Color
import tornadofx.*
import kotlin.system.exitProcess


class RootView  : View() {
    val lbl1 = label("Waiting...")
    val img = canvas()
    val master: IMasterControl by inject<MasterControl>()
    val animListView : AnimListView = AnimListView(master)
    val manyFramesView = ManyFramesView(master)

    init {
        (master.dialog as? Dialog)?.stage = primaryStage
    }

    override val root = vbox {
        menubar {
            menu("File") {
                item("Open", "Shortcut+O").action { OpenCommand.execute(master, null)}
                item("Import", "Shortcut+I").action { ImportCommand.execute(master, null)}
            }
        }
        hbox {
            add(animListView.also {
                hgrow = Priority.SOMETIMES
            })
            tabpane {
                prefWidth = 500.0
                tab("Png") {
                    add(DisplayCelSpaceView(master))
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                }
                tab("Anim"){
                    add(AnimDisplayView(master))
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                }
                hgrow = Priority.ALWAYS
            }
            vgrow = Priority.ALWAYS
            style { backgroundColor += Color.BLACK }
            add(manyFramesView.also {
                hgrow = Priority.SOMETIMES
            })
        }
    }

    init {
        primaryStage.setOnCloseRequest { exitProcess(0) }
    }
}
