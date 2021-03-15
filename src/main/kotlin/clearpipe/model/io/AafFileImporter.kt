package clearpipe.model.io

import clearpipe.model.animation.AafAnimationK
import clearpipe.model.animation.AafCelSetK
import javafx.scene.image.Image
import java.io.File


class AafPair(
    val celSet: AafCelSetK,
    val animations: List<AafAnimationK>
)

interface IAafFileImporter {
    fun import( file: File) : AafPair
}

object AafFileImporter : IAafFileImporter {
    override fun import(file: File) : AafPair {
        val (pngFile, aafFile) = getAafFiles(file)

        val img = Image(pngFile.toURI().toString())
        val aaf = AafFileLoader.loadFile(aafFile)

        val celSet = AafCelSetK(img, aaf.celRects, pngFile.nameWithoutExtension)

        return AafPair(celSet, aaf.animations.map { AafAnimationK(it.name, it.frames, celSet, it.ox, it.oy) })
    }
}