package clearpipev1.model

import clearpipev1.model.master.settings.ISettingsManager
import javafx.scene.control.TextInputDialog
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File

interface IDialog {
    fun promptForOpen(message: String) : File?
    fun promptForSave(message: String) : File?
    fun promptForString(message: String, default: String) : String?
}

class Dialog(private  val settings: ISettingsManager) : IDialog
{
    lateinit var stage : Stage

    override fun promptForString(message: String, default: String): String? {
        val dialog = TextInputDialog(default)
        dialog.contentText = message
        val result = dialog.showAndWait()
        return when {
            result.isPresent -> result.get()
            else -> null
        }
    }

    override fun promptForSave(message: String): File? {
        val fc = FileChooser()
        val file = File(settings.savePath)
        if(file.isDirectory)
            fc.initialDirectory = file
        fc.title = message
        return fc.showSaveDialog(stage)?.also { settings.savePath = it.parent }
    }

    override fun promptForOpen(message: String): File? {
        val fc = FileChooser()

        val file = File(settings.openPath)
        if(file.isDirectory)
            fc.initialDirectory = File(settings.openPath)
        fc.title = message
        return fc.showOpenDialog(stage)?.also { settings.openPath = it.parent }
    }

}