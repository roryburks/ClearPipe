package clearpipe.model.io

import clearpipe.model.animation.*
import javafx.embed.swing.SwingFXUtils
import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import old.rb.extendo.extensions.nest
import old.rb.extendo.extensions.pop
import old.rb.extendo.extensions.toLookup
import rb.vectrix.intersect.CollisionCircle
import rb.vectrix.intersect.CollisionPoint
import rb.vectrix.intersect.CollisionRigidRect
import rb.vectrix.linear.Vec2i
import rb.vectrix.mathUtil.d
import rb.vectrix.mathUtil.f
import rb.vectrix.mathUtil.i
import rb.vectrix.rectanglePacking.ModifiedSleatorAlgorithm
import rb.vectrix.rectanglePacking.PackedRectangle
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
        AafWriter.write(ra, project)
    }
}
object AafWriter {
    fun write(ra: RandomAccessFile, project: IAafProject) {
        ra.writeInt(3)  // [4]: Header

        ra.writeShort(project.animations.size)  // [2] : NumAnims
        for (anim in project.animations) {
            ra.writeUTF8nt(anim.name)   // [n] : AnimName
            ra.writeShort(anim.ox) // [2] : OriginX
            ra.writeShort(anim.oy) // [2] : OriginY
            ra.writeShort(anim.frames.size) // [2] : Num Frames
            for(frame in anim.frames) {
                ra.writeByte(frame.chunks.size) // [1] : Num chunks
                for( chunk in frame.chunks) {
                    ra.writeShort(chunk.celId)  // [2] : CelId
                    ra.writeShort(chunk.offsetX.i)   // [2]
                    ra.writeShort(chunk.offsetY.i)   // [2]
                    ra.writeInt(chunk.drawDepth)    // [4]
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
        fun write(ra: RandomAccessFile, hitbox: AafHitboxK) {
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

private typealias CelId = Pair<AafCelSetK,Int>
object CelsetCompressor {
    fun compressCelsets(project: MAafProject, name: String) {
        val (cels, remapping) = getUsedNonDuplicateCells(project.animations)

        val packedRect = ModifiedSleatorAlgorithm.pack(cels.map { Vec2i(it.region.wi, it.region.hi) })

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
        val celSet: AafCelSetK,
        val region: RectI
    )

    private fun drawAndMap(packed: PackedRectangle, cels : List<FloatingCel>, remapping: Map<CelId,FloatingCel>, name: String) : Pair<AafCelSetK,Map<Pair<AafCelSetK,Int>,Int>> {
        val dimToCelset = cels.toLookup { Vec2i(it.region.wi, it.region.hi) }

        val canvas = Canvas(packed.width.d, packed.height.d)
        val gc = canvas.graphicsContext2D
        gc.clearRect(0.0,0.0,packed.width.d, packed.height.d)

        cels.map { Pair(it,it) }.toMap()

        val map = mutableMapOf<FloatingCel,Int>()
        packed.packedRects.forEachIndexed { index, newRegion->
            val floatingCel = dimToCelset[Vec2i(newRegion.wi, newRegion.hi)]!!.pop()!!
            val (oldCelset, oldRegion) = floatingCel

            gc.drawImage(oldCelset.image, oldRegion.x1, oldRegion.y1, oldRegion.w, oldRegion.h,
                newRegion.x1, newRegion.y1, oldRegion.w, oldRegion.h)

            map[floatingCel] = index
        }

        val img = canvas.snapshot(SnapshotParameters().also { it.fill = Color.TRANSPARENT }, null)
        val newCelSet = AafCelSetK(img, packed.packedRects, name)
        return Pair(newCelSet, remapping.nest(map))
    }

    private fun getUsedNonDuplicateCells(anims: Iterable<AafAnimationK>)
            : Pair<List<FloatingCel>,Map<CelId,FloatingCel>>
    {
        // Todo: make this actually deduplicate
        val map = mutableMapOf<CelId,FloatingCel>()
        val usedCels = anims.asSequence().flatMap { anim ->
            anim.frames.asSequence().flatMap{ frame -> frame.chunks.asSequence().map { CelId(anim.celset, it.celId) } }
        }.distinct()

        val floatingCels = usedCels.map {
                FloatingCel(it.first,it.first.cels[it.second])
                    .also { cel -> map[it] = cel } }

        return Pair(floatingCels.toList(), map)
    }
}