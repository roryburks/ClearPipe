package clearpipev1.model.animation

import javafx.embed.swing.SwingFXUtils
import rb.animo.io.aaf.*
import rb.animo.io.aaf.writer.AafWriterFactory
import rb.vectrix.mathUtil.i
import rbJvm.file.writing.JvmRaWriter
import java.io.File
import java.io.RandomAccessFile
import javax.imageio.ImageIO

object AafWriting {
    fun exportFile( project: MAafProject, file: File) {
        val (pngFile, aafFile) = AafReading.getAafFiles(file)

        // Step 1: pack all celcets into one
        AafCelsetCompressor.compressCelsets(project, file.nameWithoutExtension)

        // Step 2: Save PNG
        ImageIO.write(SwingFXUtils.fromFXImage(project.celSets.single().image,null),"png", pngFile)

        // Step 3: Save AAF
        val converted = convert(project)
        if( aafFile.exists())
            aafFile.delete()
        aafFile.createNewFile()

        val ra = RandomAccessFile(aafFile, "rw")
        val writer = JvmRaWriter(ra)
        AafWriterFactory.makeWriter(4).write(writer, converted)
    }

    fun convert( project: MAafProject) : AafFile {
        val animations = project.animations.map { anim ->
            val frames = anim.frames.map { frame->
                val chunks = frame.chunks.map {
                    AafFChunk(
                        it.group,
                        it.celId,
                        it.offsetX.i,
                        it.offsetY.i,
                        it.drawDepth )
                }
                val hitboxes = frame.hboxes.map {
                    AafFHitbox(
                        it.typeId.i,
                        AafColisionMapper.mapFromVectrix(it.col) )
                }

                AafFFrame(chunks, hitboxes)
            }

            AafFAnimation(
                name = anim.name,
                ox = anim.ox,
                oy = anim.oy,
                frames  = frames )
        }

        val cels = project.celSets.single().cels.map { AafFCel(it.x1i, it.y1i, it.wi, it.hi) }

        return AafFile(-1, animations, cels)
    }
}