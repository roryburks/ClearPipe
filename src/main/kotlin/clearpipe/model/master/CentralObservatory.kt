package clearpipe.model.master

import clearpipe.model.imageData.AafAnimation
import clearpipe.model.imageData.CelSet
import clearpipe.model.imageData.IAafProject
import rb.owl.Contract
import rb.owl.IContractor
import rb.owl.bindable.IBindObserver
import rb.owl.bindable.IBindable
import rb.owl.bindable.MBindable
import rb.owl.bindable.addObserver
import rb.owl.bindableMList.*
import kotlin.contracts.contract

interface ICentralObservatory {
    val currentAafProject : MBindable<IAafProject?>

    val celSet: IBindableMList<CelSet>
    val currentCel: IBindable<CelSet?>

    val animations: IBindableMList<AafAnimation>
    val currentAnimation : IBindable<AafAnimation?>
}

class CentralObservatory(val master: MasterControl) : ICentralObservatory{
    override val currentAafProject: MBindable<IAafProject?> get() = master.projectSet.currentBind

    override val celSet: IBindableMList<CelSet> = TrackingListBinder { it.celSetsBind }
    override val currentCel: IBindable<CelSet?> = TrackingBinder { it.selectedCelBind }

    override val animations : IBindableMList<AafAnimation> = TrackingListBinder { it.animationsBind }
    override val currentAnimation: IBindable<AafAnimation?> = TrackingBinder { it.currentAnimationBind }


    // region Internal Classes
    private inner class TrackingBinder<T>(val finder : (IAafProject)->IBindable<T>) : IBindable<T?>
    {
        override val field: T? get() = currentAafProject.field?.run{ finder(this).field }
        var currentContract: Contract? = null

        init {
            currentAafProject.addObserver { new, old ->
                currentContract?.void()
                val oldF = old?.run{finder(this)}?.field
                when(new) {
                    null -> {
                        currentContract = null
                        binds.removeIf { it.observer.trigger?.invoke(null, oldF)  == null }
                    }
                    else -> {
                        val newF = finder(new).field
                        binds.removeIf { it.observer.trigger?.invoke(newF, oldF) == null}
                        currentContract = finder(new).addObserver{newt: T, oldt : T ->
                            binds.removeIf { it.observer.trigger?.invoke(newt, oldt) == null }
                        }
                    }
                }
            }
        }

        override fun addObserver(observer: IBindObserver<T?>, trigger: Boolean): Contract {
            return Contract()
                .also { it.addContractor(Contracter(observer)) }
                .also { observer.contract(it) }
        }

        private val binds = mutableListOf<Contracter>()
        private inner class Contracter(val observer: IBindObserver<T?>) : IContractor{
            init {binds.add(this)}
            override fun void() {binds.remove(this)}
        }
    }
    private inner class TrackingListBinder<T>(val finder : (IAafProject)->IBindableMList<T>) : IBindableMList<T>
    {
        override val list get() = currentAafProject.field?.run(finder)?.list ?: emptyList()
        var currentContract: Contract? = null

        val obs = object : IListTriggers<T> {
            override fun elementsAdded(index: Int, elements: Collection<T>)
            {binds.removeIf { it.observer.trigger?.elementsAdded(index, elements) == null }}
            override fun elementsRemoved(elements: Collection<T>)
            {binds.removeIf { it.observer.trigger?.elementsRemoved(elements) == null }}
            override fun elementsChanged(changes: Set<ListChange<T>>)
            {binds.removeIf { it.observer.trigger?.elementsChanged(changes) == null }}
            override fun elementsPermuted(permutation: ListPermuation)
            {binds.removeIf { it.observer.trigger?.elementsPermuted(permutation) == null }}
        }

        init {
            currentAafProject.addObserver(true) { new, old ->
                currentContract?.void()
                val oldList = old?.run(finder)?.list ?: emptyList()
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

        override fun addObserver(observer: IMutableListObserver<T>, trigger: Boolean): Contract {
            return Contract()
                .also { it.addContractor(Contracter(observer)) }
                .also { observer.contract(it) }
        }

        private val binds = mutableListOf<Contracter>()
        private inner class Contracter(val observer: IMutableListObserver<T>) : IContractor{
            init {binds.add(this)}
            override fun void() {binds.remove(this)}
        }
    }
    // endregion
}