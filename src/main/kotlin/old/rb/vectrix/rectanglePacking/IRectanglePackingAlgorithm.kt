package old.rb.vectrix.rectanglePacking

import old.rb.vectrix.linear.Vec2i
import old.rb.vectrix.shapes.RectI

data class PackedRectangle (
    val packedRects : List<RectI>
){
    val width: Int = packedRects.map{ it.x1i + it.wi}.max() ?: 0
    val height: Int = packedRects.map{ it.y1i + it.hi}.max() ?: 0
}

val NilPacked = PackedRectangle(emptyList())


interface IRectanglePackingAlgorithm {
    fun pack(toPack: List<Vec2i>) : PackedRectangle
}