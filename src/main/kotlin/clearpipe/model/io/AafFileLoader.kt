package clearpipe.model.io

import clearpipe.model.imageData.AafAnimation
import clearpipe.model.imageData.AafChunk
import clearpipe.model.imageData.AafFrame
import clearpipe.model.imageData.AafHitbox
import rb.vectrix.intersect.*
import rb.vectrix.mathUtil.d
import rb.vectrix.mathUtil.i
import rb.vectrix.mathUtil.s
import rb.vectrix.shapes.CircleD
import rb.vectrix.shapes.LineSegmentD
import rb.vectrix.shapes.RectD
import rb.vectrix.shapes.RectI
import java.io.File
import java.io.RandomAccessFile

data class FileAafAnim(val name: String, val frames: List<AafFrame>, val ox : Int = 0, val oy: Int = 0)

class AafFile(
    val animations: List<FileAafAnim>,
    val celRects: List<RectI>
)

interface IAafFileLoader{
    fun loadFile( file: File) : AafFile
}

object AafFileLoader : IAafFileLoader {
    override fun loadFile(file: File): AafFile {
        val ra = RandomAccessFile(file, "r")

        // Header
        val version = ra.readInt()

        return when( version) {
            2 -> v2Loader(ra)
            3 -> v3Loader(ra)
            else -> throw NotImplementedError("Aaf Version not supported")
        }
    }

    private fun v3Loader(ra: RandomAccessFile) : AafFile {

        // Animations
        val numAnims = ra.readUnsignedShort()
        val anims = List(numAnims) {
            val animName = ra.readUTF8nt()
            val ox = ra.readShort()
            val oy = ra.readShort()
            val numFrames = ra.readUnsignedShort()
            val frames = List(numFrames) {
                val numChunks = ra.readUnsignedByte()
                val chunks = List(numChunks) {
                    AafChunk(
                        celId = ra.readUnsignedShort(),
                        offsetX = ra.readShort(),
                        offsetY = ra.readShort(),
                        drawDepth = ra.readInt())
                }
                val numHboxes = ra.readUnsignedByte()
                val hitbox = MutableList(numHboxes) {
                    val typeId = ra.readUnsignedByte().s
                    AafHitbox(typeId, loadHitbox(ra))
                }
                AafFrame(chunks, hitbox)
            }
            FileAafAnim(animName, frames, ox.i, oy.i)
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

    fun loadHitbox(ra: RandomAccessFile) : CollisionObject{
        return when( val colTypeId = ra.readUnsignedByte()) {
            FileConsts.ColKind_Point -> CollisionPoint(ra.readFloat().d, ra.readFloat().d)
            FileConsts.ColKind_RigidRect -> CollisionRigidRect(RectD(ra.readFloat().d, ra.readFloat().d, ra.readFloat().d, ra.readFloat().d))
            FileConsts.ColKind_Circle -> CollisionCircle(CircleD.Make(ra.readFloat().d, ra.readFloat().d, ra.readFloat().d))
            FileConsts.ColKind_LineSegment -> CollisionLineSegment(LineSegmentD(ra.readFloat().d,ra.readFloat().d,ra.readFloat().d,ra.readFloat().d))
            else -> throw NotImplementedError("Unrecognized Collision Type: $colTypeId")
        }
    }

    private fun v2Loader(ra: RandomAccessFile) : AafFile {

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
            FileAafAnim(animName, frames)
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