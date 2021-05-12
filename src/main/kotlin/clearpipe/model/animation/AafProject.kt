package clearpipe.model.animation

import rb.extendo.extensions.toHashMap
import old.rb.owl.bindable.Bindable
import old.rb.owl.bindableMList.BindableMList
import old.rb.owl.bindableMList.IBindableMList

data class AafProjectImportSet(
    val animations: List<AafAnimationK>,
    val celset: AafCelSetK )


interface IAafProject {
    val animationsBind: IBindableMList<AafAnimationK>
    val animations: List<AafAnimationK>
    val currentAnimationBind : Bindable<AafAnimationK?>
    var currentAnimation : AafAnimationK?

    val celSetsBind: IBindableMList<AafCelSetK>
    val celSets: List<AafCelSetK>
    val selectedCelBind : Bindable<AafCelSetK?>
    var selectedCel : AafCelSetK?

    fun import(importSet: AafProjectImportSet)
}

interface MAafProject : IAafProject {
    override val celSets : MutableList<AafCelSetK>
}

class AafProject : MAafProject {
    override val animationsBind = BindableMList<AafAnimationK>()
    override val animations get() = animationsBind.list
    override val currentAnimationBind = Bindable<AafAnimationK?>(null)
    override var currentAnimation: AafAnimationK? by currentAnimationBind
    
    override val celSetsBind = BindableMList<AafCelSetK>()
    override val celSets get() = celSetsBind.list
    override val selectedCelBind = Bindable<AafCelSetK?>(null)
    override var selectedCel: AafCelSetK? by selectedCelBind

    override fun import(importSet: AafProjectImportSet) {
        val animations = importSet.animations
        val celset = importSet.celset
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

