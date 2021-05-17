package clearpipev1.model.animation

import javafx.scene.image.Image
import rb.animo.io.aaf.AafColisionMapper
import rb.animo.io.aaf.AafFile
import rb.animo.io.aaf.reader.AafReaderFactory
import rb.file.BufferedFileReader
import rb.vectrix.mathUtil.s
import rb.vectrix.shapes.RectI
import rbJvm.file.JvmInputStreamFileReader
import java.io.File

object AafReading {

    fun loadImportSet(pngFile: File, aafFile: File) : AafProjectImportSet{
        val img = Image(pngFile.toURI().toString())

        // Read AafFile
        val aafFileReader = BufferedFileReader( JvmInputStreamFileReader(aafFile.inputStream()))
        val aafReader = AafReaderFactory.readVersionAndGetReader(aafFileReader)
        val aaf = aafReader.read(aafFileReader)

        return convert(aaf, img, pngFile.nameWithoutExtension)
    }

    fun convert( aaf: AafFile, img: Image, celSetName: String) : AafProjectImportSet {
        val celSet = AafCelSetK(
            img,
            aaf.cels.map { RectI(it.x, it.y, it.w, it.h) },
            celSetName )

        val animations = aaf.animations.map { anim->
            val frames = anim.frames.map { frame->
                val chunks = frame.chunks.map { chunk->
                    AafChunkK(
                        celId = chunk.celId,
                        group = chunk.group,
                        offsetX = chunk.offsetX.s,
                        offsetY = chunk.offsetY.s,
                        drawDepth = chunk.drawDepth )
                }
                val hboxes = frame.hitboxes.map {
                    val col = AafColisionMapper.mapToVectrix(it.col)
                    AafHitboxK(it.typeId.s, col)
                }

                AafFrameK( chunks, listOf() )
            }

            AafAnimationK(
                anim.name,
                frames,
                celSet,
                anim.ox,
                anim.oy )
        }

        return  AafProjectImportSet( animations, celSet)
    }

    fun getAafFiles(file: File) : Pair<File, File> {
        val filename = file.absolutePath
        return when(val ext = file.extension.toLowerCase()) {
            "png" -> Pair(file, File(filename.substring(0, filename.length-3)+"aaf"))
            else -> Pair(File(filename.substring(0, filename.length - ext.length) + "png"), file)
        }
    }
}

