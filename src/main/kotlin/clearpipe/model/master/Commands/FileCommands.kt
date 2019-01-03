package clearpipe.model.master.Commands

import clearpipe.model.imageData.AafProject
import clearpipe.model.imageData.IAafProject
import clearpipe.model.io.AafFileImporter
import clearpipe.model.master.IMasterControl

interface ICommand {
    fun execute(master: IMasterControl, obj: Any?) : Boolean
    val name: String
}

object OpenCommand : ICommand{
    override val name: String get() = "Open"
    override fun execute(master: IMasterControl, obj: Any?): Boolean {
        val toOpen = master.dialog.promptForOpen("Open Aaf File") ?: return true
        val aaf = AafFileImporter.import(toOpen)
        val project = AafProject()

        project.import(aaf.animations, aaf.celSet)
        master.projectSet.add(project)

        return true
    }
}

object ImportCommand : ICommand{
    override val name: String get() = "Import"
    override fun execute(master: IMasterControl, obj: Any?): Boolean {
        val workspace = obj as? IAafProject ?: master.projectSet.current
                ?: return OpenCommand.execute(master, obj)
        val toOpen = master.dialog.promptForOpen("Import Aaf File") ?: return true
        val aaf = AafFileImporter.import(toOpen)

        workspace.import(aaf.animations, aaf.celSet)

        return true
    }
}


object SaveCommand : ICommand {
    override val name: String get() = "Save"
    override fun execute(master: IMasterControl, obj: Any?): Boolean {
        val workspace = obj as? IAafProject ?: master.projectSet.current ?: return false
        val toSave = master.dialog.promptForSave("Save Aaf File") ?: return true

        return true
    }


}