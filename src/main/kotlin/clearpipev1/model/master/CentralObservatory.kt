package clearpipev1.model.master

import clearpipev1.model.animation.AafAnimationK
import clearpipev1.model.animation.AafCelSetK
import clearpipev1.model.animation.IAafProject
import clearpipev1.model.animation.MAafProject
import rb.global.IContract
import rb.owl.bindable.Bindable
import rb.owl.bindable.IBindObserver
import rb.owl.bindable.IBindable
import rb.owl.bindable.addObserver
import rb.owl.bindableMList.*

interface ICentralObservatory {
    val currentAafProject : Bindable<MAafProject?>

    val celSet: IBindableMList<AafCelSetK>
    val currentCel: IBindable<AafCelSetK?>

    val animations: IBindableMList<AafAnimationK>
    val currentAnimation : IBindable<AafAnimationK?>
}

class CentralObservatory(val master: MasterControl) : ICentralObservatory{
    override val currentAafProject: Bindable<MAafProject?> get() = master.projectSet.currentBind

    override val celSet: IBindableMList<AafCelSetK> = TrackingListBinder { it.celSetsBind }
    override val currentCel: IBindable<AafCelSetK?> = TrackingBinder { it.selectedCelBind }

    override val animations : IBindableMList<AafAnimationK> = TrackingListBinder { it.animationsBind }
    override val currentAnimation: IBindable<AafAnimationK?> = TrackingBinder { it.currentAnimationBind }


    // region Internal Classes
    private inner class TrackingBinder<T>(val finder : (IAafProject)->IBindable<T>) : IBindable<T?>
    {
        override val field: T? get() = currentAafProject.field?.run{ finder(this).field }
        var currentContract: IContract? = null

        override fun addObserver(observer: IBindObserver<T?>, trigger: Boolean): IContract = Contracter(observer)

        private val binds = mutableListOf<Contracter>()
        private inner class Contracter(val observer: IBindObserver<T?>) : IContract {
            init {binds.add(this)}
            override fun void() {binds.remove(this)}
        }

        init {
            currentAafProject.addObserver { new, old ->
                currentContract?.void()
                val oldF = old?.run{finder(this)}?.field
                when(new) {
                    null -> {
                        currentContract = null
                        binds.removeIf { it.observer.triggers?.forEach { it.invoke(null, oldF)}  == null }
                    }
                    else -> {
                        val newF = finder(new).field
                        binds.removeIf { it.observer.triggers?.forEach { it.invoke(newF, oldF)} == null}
                        currentContract = finder(new).addObserver{newt: T, oldt : T ->
                            binds.removeIf { it.observer.triggers?.forEach { it.invoke(newt, oldt)} == null }
                        }
                    }
                }
            }
        }
    }
    private inner class TrackingListBinder<T>(val finder : (IAafProject)->IBindableMList<T>) : IBindableMList<T>
    {
        override val list get() = currentAafProject.field?.run(finder)?.list ?: mutableListOf()
        var currentContract: IContract? = null

        val obs = object : IListTriggers<T> {
            override fun elementsAdded(index: Int, elements: Collection<T>)
            {binds.removeIf { it.observer.triggers?.forEach { it.elementsAdded(index, elements)} == null }}
            override fun elementsRemoved(elements: Collection<T>)
            {binds.removeIf { it.observer.triggers?.forEach { it.elementsRemoved(elements)} == null }}
            override fun elementsChanged(changes: Set<ListChange<T>>)
            {binds.removeIf { it.observer.triggers?.forEach { it.elementsChanged(changes)} == null }}
            override fun elementsPermuted(permutation: ListPermuation)
            {binds.removeIf { it.observer.triggers?.forEach { it.elementsPermuted(permutation)} == null }}
        }
        override fun addObserver(observer: IMutableListObserver<T>, trigger: Boolean): IContract = Contracter(observer)

        private val binds = mutableListOf<Contracter>()
        private inner class Contracter(val observer: IMutableListObserver<T>) : IContract {
            init {binds.add(this)}
            override fun void() {binds.remove(this)}
        }

        init {
            currentAafProject.addObserver(true) { new, old ->
                currentContract?.void()
                val oldList = old?.run(finder)?.list ?: mutableListOf()
                obs.elementsRemoved(oldList)
                if( new != null) {
                    val newBind = new.run(finder)
                    val newList =newBind.list
                    currentContract = newBind.addObserver(obs.observer())
                    obs.elementsAdded(0, newList)
                }
                else {
                    currentContract = null
                }
            }
        }
    }
    // endregion
}