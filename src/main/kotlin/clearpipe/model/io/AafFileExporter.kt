package clearpipe.model.io

import clearpipe.model.imageData.AafHitbox
import clearpipe.model.imageData.CelSet
import clearpipe.model.imageData.IAafProject
import clearpipe.model.imageData.MAafProject
import javafx.embed.swing.SwingFXUtils
import javafx.scene.canvas.Canvas
import rb.extendo.extensions.*
import rb.vectrix.calculate.ModifiedSleatorAlgorithm
import rb.vectrix.calculate.PackedRectangle
import rb.vectrix.intersect.CollisionCircle
import rb.vectrix.intersect.CollisionPoint
import rb.vectrix.intersect.CollisionRigidRect
import rb.vectrix.linear.Vec2i
import rb.vectrix.mathUtil.d
import rb.vectrix.mathUtil.f
import rb.vectrix.mathUtil.i
import rb.vectrix.shapes.RectI
import java.io.File
import java.io.RandomAccessFile
import javax.imageio.ImageIO

object AafFileExporter {
    fun exportFile(project: MAafProject, file: File) {
        val (pngFile, aafFile) = getAafFiles(file)

        // Step 1: pack all
        CelsetCompressor.compressCelsets(project, file.nameWithoutExtension)

        // Step 2: Save Png
        ImageIO.write(SwingFXUtils.fromFXImage(project.celSets.single().image,null),"png", pngFile)

        // Step 3: Save Aaf
        if( aafFile.exists())
            aafFile.delete()
        aafFile.createNewFile()

        val ra = RandomAccessFile(aafFile, "rw")
    }
}
object AafWriter {
    fun write(ra: RandomAccessFile, project: IAafProject) {
        ra.writeInt(2)  // [4]: Header

        ra.writeShort(project.animations.size)  // [2] : NumAnims
        for (anim in project.animations) {
            ra.writeUTF8nt(anim.name)   // [n] : AnimName
            ra.writeShort(anim.frames.size) // [2] : Num Frames
            for(frame in anim.frames) {
                ra.writeShort(frame.ox) // [2] : OriginX
                ra.writeShort(frame.oy) // [2] : OriginY
                ra.writeByte(frame.chunks.size) // [1] : Num chunks
                for( chunk in frame.chunks) {
                    ra.writeShort(chunk.celId)  // [2] : CelId
                    ra.writeShort(chunk.offsetX.i)   // [2]
                    ra.writeShort(chunk.offsetY.i)   // [2]
                }
                ra.writeByte(frame.hboxes.size) // [1]
                frame.hboxes.forEach { HitboxWriter.write(ra, it) } // [n]
            }
        }

        val celSet = project.celSets.single()
        ra.writeShort(celSet.cels.size) // [2]
        for (cel in celSet.cels) {
            // [8]
            ra.writeShort(cel.x1i)
            ra.writeShort(cel.y1i)
            ra.writeShort(cel.wi)
            ra.writeShort(cel.hi)
        }
    }

    object HitboxWriter {
        fun write(ra: RandomAccessFile, hitbox: AafHitbox) {
            ra.writeByte(hitbox.typeId.i)
            when(val col = hitbox.col) {
                is CollisionPoint -> {
                    ra.writeByte(FileConsts.ColKind_Point)
                    ra.writeFloat(col.x.f)
                    ra.writeFloat(col.y.f)
                }
                is CollisionRigidRect -> {
                    ra.writeByte(FileConsts.ColKind_RigidRect)
                    ra.writeFloat(col.rect.x1.f)
                    ra.writeFloat(col.rect.y1.f)
                    ra.writeFloat(col.rect.w.f)
                    ra.writeFloat(col.rect.h.f)
                }
                is CollisionCircle -> {
                    ra.writeByte(FileConsts.ColKind_Circle)
                    ra.writeFloat(col.circle.x.f)
                    ra.writeFloat(col.circle.y.f)
                    ra.writeFloat(col.circle.r.f)
                }
            }

        }
    }
}

private typealias CelId = Pair<CelSet,Int>
object CelsetCompressor {
    fun compressCelsets(project: MAafProject, name: String) {
        val (cels, remapping) = getDeduplicatedCelsets(project.celSets)

        val packedRect = ModifiedSleatorAlgorithm(cels.map { Vec2i(it.region.wi, it.region.hi) })

        val (newCelset, remappingMap) = drawAndMap(packedRect, cels, remapping, name)

        for( anim in project.animations) {
            val oldCelset = anim.celset
            anim.celset = newCelset

            for (frame in anim.frames)
                for( chunk in frame.chunks)
                    chunk.celId = remappingMap[Pair(oldCelset,chunk.celId)]!!
        }

        project.celSets.clear()
        project.celSets.add(newCelset)
    }

    private data class FloatingCel(
        val celSet: CelSet,
        val region: RectI)

    private fun drawAndMap( packed: PackedRectangle, cels : List<FloatingCel>, remapping: Map<CelId,FloatingCel>, name: String) : Pair<CelSet,Map<Pair<CelSet,Int>,Int>> {
        val dimToCelset = cels.toLookup { Vec2i(it.region.wi, it.region.hi) }

        val canvas = Canvas(packed.width.d, packed.height.d)
        val gc = canvas.graphicsContext2D

        cels.map { Pair(it,it) }.toMap()

        val map = mutableMapOf<FloatingCel,Int>()
        packed.packedRects.forEachIndexed { index, newRegion->
            val floatingCel = dimToCelset[Vec2i(newRegion.wi, newRegion.hi)]!!.pop()!!
            val (oldCelset, oldRegion) = floatingCel

            gc.drawImage(oldCelset.image, oldRegion.x1, oldRegion.y1, oldRegion.w, oldRegion.h,
                newRegion.x1, newRegion.y1, oldRegion.w, oldRegion.h)

            map[floatingCel] = index
        }

        val img = canvas.snapshot(null, null)
        val newCelSet = CelSet(img, packed.packedRects, name)
        return Pair(newCelSet, remapping.nest(map))
    }

    private fun getDeduplicatedCelsets(original : List<CelSet>) : Pair<List<FloatingCel>,Map<CelId,FloatingCel>> {
        // Todo: make this actually deduplicate
        val map = mutableMapOf<CelId,FloatingCel>()
        val floatingCels = original.flatMap { celset ->
            celset.cels.mapIndexed { index, region ->
                FloatingCel(celset, region).also { map[CelId(celset,index)] = it } } }
        return Pair(floatingCels, map)
    }
}