package clearpipe.model.imageData

import clearpipe.model.DrawContract
import javafx.scene.image.Image
import old.rb.extendo.extensions.toHashMap
import rb.owl.bindable.Bindable
import rb.owl.bindableMList.BindableMList
import rb.owl.bindableMList.IBindableMList
import old.rb.vectrix.intersect.CollisionObject
import old.rb.vectrix.mathUtil.MathUtil
import old.rb.vectrix.mathUtil.d
import old.rb.vectrix.shapes.RectI

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

interface MAafProject : IAafProject {
    override val celSets : MutableList<CelSet>
}

class AafProject : MAafProject {
    override val animationsBind = BindableMList<AafAnimation>()
    override val animations get() = animationsBind.list
    override val currentAnimationBind = Bindable<AafAnimation?>(null)
    override var currentAnimation: AafAnimation? by currentAnimationBind
    
    override val celSetsBind = BindableMList<CelSet>()
    override val celSets get() = celSetsBind.list
    override val selectedCelBind = Bindable<CelSet?>(null)
    override var selectedCel: CelSet? by selectedCelBind

    override fun import(animations: List<AafAnimation>, celset: CelSet) {
        val nameMap = animations.toHashMap({it.name}, {it})

        this.animations
            .removeAll {
                val mapped = nameMap[it.name] ?: return@removeAll false
                println("removingL ${it.name}")
                mapped.ox = it.ox
                mapped.oy = it.oy
                mapped.frames.zip(it.frames).forEach { (new,old) ->new.addHBoxes(old.hboxes)}
                true
            }

        this.animations.addAll(animations)
        celSets.add(celset)
        selectedCel = selectedCel ?: celset
        currentAnimation = currentAnimation ?: animations.firstOrNull()
    }
}

class AafAnimation(
    var name: String,
    val frames: List<AafFrame>,
    var celset: CelSet,
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

class AafFrame
constructor(
    val chunks: List<AafChunk>,
    hboxes: List<AafHitbox> = listOf())
{
    private val _hboxes : MutableList<AafHitbox>
    val hboxes : List<AafHitbox> get() = _hboxes

    init {
        _hboxes = hboxes.toMutableList()
        _hboxes.forEach { it.context = this }
    }

    fun addHBox(hitbox: AafHitbox) {
        _hboxes.add(hitbox)
        hitbox.context = this
    }
    fun addHBoxes(hitbox: Collection<AafHitbox>) {
        _hboxes.addAll(hitbox)
        hitbox.forEach { it.context = this }
    }
    fun removeHBox(hitbox: AafHitbox) {
        _hboxes.remove(hitbox)
    }
    fun clearHBoxes() = _hboxes.clear()
}

class AafChunk(
    var celId: Int,
    val offsetX: Short,
    val offsetY: Short,
    val drawDepth: Int)

class CelSet(val image: Image, val cels: List<RectI>, val name: String) {
    override fun toString() = name
}

data class AafHitbox
    constructor(
    var typeId: Short,
    var col: CollisionObject)
{
    lateinit var context: AafFrame
}