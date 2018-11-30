package clearpipe.model

import javafx.scene.control.TextInputDialog
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File

interface IDialog {
    fun promptForOpen(message: String) : File?
    fun promptForString(message: String, default: String) : String?
}

class Dialog() : IDialog {
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

    override fun promptForOpen(message: String): File? {
        val fc = FileChooser()
        fc.title = message
        return fc.showOpenDialog(stage)
    }

}