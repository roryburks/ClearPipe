package clearpipe.model.imageData

import clearpipe.model.DrawContract
import javafx.scene.image.Image
import rb.owl.bindable.Bindable
import rb.owl.bindableMList.BindableMList
import rb.owl.bindableMList.IBindableMList
import rb.vectrix.mathUtil.MathUtil
import rb.vectrix.mathUtil.d
import rb.vectrix.mathUtil.i
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

    fun getDraws(frame: Int) : List<DrawContract>{
        val met = MathUtil.cycle(0, frames.size, frame)
        return frames[met]
            .chunks
            .map { DrawContract(celset.image, celset.cels[it.celId], it.offsetX.d, it.offsetY.d) }
    }
}
class AafFrame(
    val chunks: List<AafChunk>
)
class AafChunk(
    val celId: Int,
    val offsetX: Short,
    val offsetY: Short,
    val drawDepth: Int)

class CelSet(val image: Image, val cels: List<RectI>, val name: String) {
    override fun toString() = name
}