package clearpipe.model.animation

import clearpipe.model.DrawContract
import javafx.scene.image.Image
import old.rb.vectrix.intersect.CollisionObject
import old.rb.vectrix.mathUtil.MathUtil
import old.rb.vectrix.mathUtil.d
import old.rb.vectrix.shapes.RectI


class AafAnimationK(
    var name: String,
    val frames: List<AafFrameK>,
    var celset: AafCelSetK,
    var ox: Int = 0,
    var oy: Int = 0)
{
    override fun toString() = name

    val x1 get() = frames.asSequence()
        .flatMap { frame -> frame.chunks.asSequence().map { it.offsetX - ox} }
        .min()
    val y1 get() = frames.asSequence()
        .flatMap { frame -> frame.chunks.asSequence().map { it.offsetY - oy } }
        .min()
    val x2 get() = frames.asSequence()
        .flatMap { frame -> frame.chunks.asSequence().map { it.offsetX + celset.cels[it.celId].wi - ox } }
        .max()
    val y2 get() = frames.asSequence()
        .flatMap { frame -> frame.chunks.asSequence().map { it.offsetY + celset.cels[it.celId].hi - oy } }
        .max()

    fun getFrame(met: Int) =  frames[MathUtil.cycle(0, frames.size, met)]

    fun getDraws(met: Int) : List<DrawContract>{
        val frame = getFrame(met)
        return frame
            .chunks
            .map { DrawContract(celset.image, celset.cels[it.celId], it.offsetX.d - ox, it.offsetY.d  - oy) }
    }
}

class AafFrameK
constructor(
    val chunks: List<AafChunkK>,
    hboxes: List<AafHitboxK> = listOf())
{
    private val _hboxes : MutableList<AafHitboxK>
    val hboxes : List<AafHitboxK> get() = _hboxes

    init {
        _hboxes = hboxes.toMutableList()
        _hboxes.forEach { it.context = this }
    }

    fun addHBox(hitbox: AafHitboxK) {
        _hboxes.add(hitbox)
        hitbox.context = this
    }
    fun addHBoxes(hitbox: Collection<AafHitboxK>) {
        _hboxes.addAll(hitbox)
        hitbox.forEach { it.context = this }
    }
    fun removeHBox(hitbox: AafHitboxK) {
        _hboxes.remove(hitbox)
    }
    fun clearHBoxes() = _hboxes.clear()
}

class AafChunkK(
    var celId: Int,
    val group: Char,
    val offsetX: Short,
    val offsetY: Short,
    val drawDepth: Int)

class AafCelSetK(val image: Image, val cels: List<RectI>, val name: String) {
    override fun toString() = name
}

data class AafHitboxK
constructor(
    var typeId: Short,
    var col: CollisionObject
)
{
    lateinit var context: AafFrameK
}
