package clearpipe.model.io

import clearpipe.model.imageData.AafAnimation
import clearpipe.model.imageData.AafChunk
import clearpipe.model.imageData.AafFrame
import rb.vectrix.mathUtil.i
import rb.vectrix.shapes.RectI
import java.io.File
import java.io.RandomAccessFile

class AafFile(
    val animations: List<AafAnimation>,
    val celRects: List<RectI>
)

interface IAafFileLoader{
    fun loadFile( file: File) : AafFile
}

object AafFileLoader : IAafFileLoader {
    override fun loadFile(file: File): AafFile {
        val ra = RandomAccessFile(file, "r")

        // Header
        val header = ra.readInt()

        // Animations
        val numAnims = ra.readShort()
        val anims = List(numAnims.i) {
            val animName = ra.readUTF8nt()
            val numFrames = ra.readUnsignedShort()
            val frames = List(numFrames) {
                val numChunks = ra.readUnsignedShort()
                val chunks = List(numChunks) {
                    AafChunk(
                        celId = ra.readUnsignedShort(),
                        offsetX = ra.readShort(),
                        offsetY = ra.readShort(),
                        drawDepth = ra.readInt())
                }
                AafFrame(chunks)
            }
            AafAnimation(animName, frames)
        }

        // Cels
        val numCels = ra.readUnsignedShort()
        val cels = List(numCels) {
            val x = ra.readShort()
            val y = ra.readShort()
            val w = ra.readUnsignedShort()
            val h = ra.readUnsignedShort()
            RectI(x.i, y.i, w, h)
        }

        return AafFile(anims, cels)
    }

}