package clearpipe.model.io

import javafx.scene.image.Image
import java.io.File


class AafPair(
    val img: Image
)

interface IAafFileImporter {
    fun import( file: File) : AafPair
}

object AafFileImporter : IAafFileImporter {
    override fun import(file: File) : AafPair {
        val (pngFile, aafFile) = getFiles(file)
        println(pngFile)

        return AafPair(Image(pngFile.toURI().toString()))
    }

    fun getFiles(file: File) : Pair<File,File> {
        val filename = file.absolutePath
        return when(val ext = file.extension.toLowerCase()) {
            "png" -> Pair(file, File(filename.substring(0, filename.length-3)+"aaf"))
            else -> Pair(File(filename.substring(0, filename.length - ext.length) + "png"), file)
        }
    }
}