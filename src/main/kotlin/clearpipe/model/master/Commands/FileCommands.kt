package clearpipe.model.master.Commands

import clearpipe.model.animation.AafProject
import clearpipe.model.animation.AafReading
import clearpipe.model.animation.IAafProject
import clearpipe.model.animation.MAafProject
import clearpipe.model.io.AafFileExporter
import clearpipe.model.io.AafFileImporter
import clearpipe.model.master.IMasterControl
import javafx.scene.image.Image
import rb.animo.io.aaf.reader.AafReaderFactory
import rb.file.BufferedFileReader
import rbJvm.file.JvmInputStreamFileReader
import java.io.File

interface ICommand {
    fun execute(master: IMasterControl, obj: Any?) : Boolean
    val name: String
}

object OpenCommand : ICommand{
    override val name: String get() = "Open"
    override fun execute(master: IMasterControl, obj: Any?): Boolean {
        val toOpen = master.dialog.promptForOpen("Open Aaf File") ?: return true

        val (pngFile, aafFile) = AafReading.getAafFiles(toOpen)
        val img = Image(pngFile.toURI().toString())

        // Read AafFile
        //val bytes=  aafFile.inputStream().readBytes()
        val aafFileReader = BufferedFileReader( JvmInputStreamFileReader(aafFile.inputStream()))
        val aafReader = AafReaderFactory.readVersionAndGetReader(aafFileReader)
        val aaf = aafReader.read(aafFileReader)

        // Create new Project and import it
        //val aaf = AafFileImporter.import(toOpen)
        val project = AafProject()
        val importSet = AafReading.convert(aaf, img, pngFile.nameWithoutExtension)

        project.import(importSet)
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

        //workspace.import(aaf.animations, aaf.celSet)

        return true
    }
}


object SaveCommand : ICommand {
    override val name: String get() = "Save"
    override fun execute(master: IMasterControl, obj: Any?): Boolean {
        val workspace = obj as? MAafProject ?: master.projectSet.current ?: return false
        var toSave = master.dialog.promptForSave("Save Aaf File") ?: return true
        if( toSave.extension == "" ) {
            toSave = File(toSave.absolutePath + ".aaf")
        }
        AafFileExporter.exportFile(workspace, toSave)
        return true
    }


}