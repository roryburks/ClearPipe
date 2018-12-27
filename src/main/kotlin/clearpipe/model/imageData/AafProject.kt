package clearpipe.model.imageData

import clearpipe.model.DrawContract
import javafx.scene.image.Image
import rb.owl.bindable.Bindable
import rb.owl.bindableMList.BindableMList
import rb.owl.bindableMList.IBindableMList
import rb.vectrix.intersect.CollisionObject
import rb.vectrix.mathUtil.MathUtil
import rb.vectrix.mathUtil.d
import rb.vectrix.shapes.RectI

interface IAafProject {
    val animationsBind: IBindableMList<AafAnimation>
    val animations: List<AafAnimation>
    val currentAnimationBind : Bindable<AafAnimation?>
    var currentAnimation : AafAnimation?

    val celSetsBind: IBindableMList<CelSet>
    val celSets: List<CelSet>
    val selectedCelBind : Bindable<CelSet?>
    var selectedCel : CelSet?

    fun import( animations: List<AafAnimation>, celset: CelSet)
}

class AafProject : IAafProject {
    override val animationsBind = BindableMList<AafAnimation>()
    override val animations get() = animationsBind.list
    override val currentAnimationBind = Bindable<AafAnimation?>(null)
    override var currentAnimation: AafAnimation? by currentAnimationBind
    
    override val celSetsBind = BindableMList<CelSet>()
    override val celSets get() = celSetsBind.list
    override val selectedCelBind = Bindable<CelSet?>(null)
    override var selectedCel: CelSet? by selectedCelBind

    override fun import(animations: List<AafAnimation>, celset: CelSet) {
        this.animations.addAll(animations)
        celSets.add(celset)
        selectedCel = selectedCel ?: celset
        currentAnimation = currentAnimation ?: animations.firstOrNull()
    }
}

class AafAnimation(
    var name: String,
    val frames: List<AafFrame>,
    val celset: CelSet)
{
    override fun toString() = name

    val x1 = frames.asSequence()
        .flatMap { frame -> frame.chunks.asSequence().map { it.offsetX - frame.ox} }
        .min()
    val y1 = frames.asSequence()
        .flatMap { frame -> frame.chunks.asSequence().map { it.offsetY - frame.oy } }
        .min()
    val x2 = frames.asSequence()
        .flatMap { frame -> frame.chunks.asSequence().map { it.offsetX + celset.cels[it.celId].wi - frame.ox } }
        .max()
    val y2 = frames.asSequence()
        .flatMap { frame -> frame.chunks.asSequence().map { it.offsetY + celset.cels[it.celId].hi - frame.oy } }
        .max()

    fun getFrame(met: Int) =  frames[MathUtil.cycle(0, frames.size, met)]

    fun getDraws(met: Int) : List<DrawContract>{
        val frame = getFrame(met)
        return frame
            .chunks
            .map { DrawContract(celset.image, celset.cels[it.celId], it.offsetX.d - frame.ox, it.offsetY.d  - frame.oy) }
    }
}

class AafFrame(
    val chunks: List<AafChunk>,
    var ox: Int = 0,
    var oy: Int = 0,
    val hboxes: MutableList<AafHitbox> = mutableListOf())
{
}

class AafChunk(
    val celId: Int,
    val offsetX: Short,
    val offsetY: Short,
    val drawDepth: Int)

class CelSet(val image: Image, val cels: List<RectI>, val name: String) {
    override fun toString() = name
}

class AafHitbox(
    var typeId: Short,
    var col: CollisionObject)