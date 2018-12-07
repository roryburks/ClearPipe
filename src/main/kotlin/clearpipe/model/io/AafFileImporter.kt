package clearpipe.model.io

import clearpipe.model.imageData.AafAnimation
import clearpipe.model.imageData.CelSet
import javafx.scene.image.Image
import java.io.File


class AafPair(
    val celSet: CelSet,
    val animations: List<AafAnimation>
)

interface IAafFileImporter {
    fun import( file: File) : AafPair
}

object AafFileImporter : IAafFileImporter {
    override fun import(file: File) : AafPair {
        val (pngFile, aafFile) = getFiles(file)

        val img = Image(pngFile.toURI().toString())
        val aaf = AafFileLoader.loadFile(aafFile)

        val celSet = CelSet(img, aaf.celRects, pngFile.nameWithoutExtension)

        return AafPair(celSet, aaf.animations.map { AafAnimation(it.name, it.frames, celSet) })
    }

    fun getFiles(file: File) : Pair<File,File> {
        val filename = file.absolutePath
        return when(val ext = file.extension.toLowerCase()) {
            "png" -> Pair(file, File(filename.substring(0, filename.length-3)+"aaf"))
            else -> Pair(File(filename.substring(0, filename.length - ext.length) + "png"), file)
        }
    }
}