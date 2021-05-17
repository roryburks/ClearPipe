package clearpipev1.ui

import clearpipev1.ui.mainViews.AnimListView
import javafx.scene.layout.Priority
import clearpipev1.model.Dialog
import clearpipev1.model.master.IMasterControl
import clearpipev1.model.master.MasterControl
import clearpipev1.model.master.Commands.ImportCommand
import clearpipev1.model.master.Commands.OpenCommand
import clearpipev1.model.master.Commands.SaveCommand
import clearpipev1.ui.mainViews.ManyFramesView
import clearpipev1.ui.mainViews.center.AnimDisplayView
import clearpipev1.ui.mainViews.center.DisplayCelSpaceView
import clearpipev1.ui.mainViews.center.hitbox.HitboxView
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
                item("Save", "Shortcut+S").action { SaveCommand.execute(master, null)}
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
                tab("Hitbox") {
                    add(HitboxView(master))
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                }
                hgrow = Priority.ALWAYS
            }
            vgrow = Priority.ALWAYS
            style { backgroundColor += Color.DIMGRAY }
            add(manyFramesView.also {
                hgrow = Priority.SOMETIMES
            })
        }
    }

    init {
        primaryStage.setOnCloseRequest { exitProcess(0) }
    }
}
