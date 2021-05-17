package clearpipev1.model.animation

import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import rb.extendo.extensions.nest
import rb.extendo.extensions.pop
import rb.extendo.extensions.toLookup
import rb.vectrix.linear.Vec2i
import rb.vectrix.mathUtil.d
import rb.vectrix.rectanglePacking.ModifiedSleatorAlgorithm
import rb.vectrix.rectanglePacking.PackedRectangle
import rb.vectrix.shapes.RectI

private typealias CelId = Pair<AafCelSetK,Int>

// TODO: The logic which can be done acting on AafFile contracts, abstract those out
object AafCelsetCompressor {
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