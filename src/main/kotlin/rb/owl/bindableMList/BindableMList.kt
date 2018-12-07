package rb.owl.bindableMList

import rb.extendo.dataStructures.SinglySequence
import rb.extendo.extensions.mapRemoveIfNull
import rb.extendo.extensions.then
import rb.owl.Contract
import rb.owl.IContractor


interface IBindableMList<T> {
    val list: List<T>
    fun addObserver( observer: IMutableListObserver<T>, trigger: Boolean = false) : Contract
}

interface MBindableMList<T> : IBindableMList<T> {
    override val list: MutableList<T>
    fun bindTo( root: MBindableMList<T>) : Contract
    fun respondToBind( derived: MBindableMList<T>, contract: Contract)
    val triggers: Sequence<IListTriggers<T>>
}

class BindableMList<T>(list: Collection<T> = emptyList()) :
    MBindableMList<T>
{
    private var underlying = Underlying(list, this)
    override val list : MutableList<T> get() = underlying.list

    private val externalBinds = mutableListOf<ExternalBindContract>()
    private val contracts = mutableListOf<ObserverContract>()

    private val myTriggers : Sequence<IListTriggers<T>>
        get() = contracts.mapRemoveIfNull { it.observer.trigger }
            .then(externalBinds.asSequence().flatMap { it.external.triggers })

    override val triggers: Sequence<IListTriggers<T>> get() = underlying.triggers
        .then(SinglySequence(underlying.externalTrigger))

    override fun addObserver(observer: IMutableListObserver<T>, trigger: Boolean) =
        Contract()
            .also{it.addContractor(ObserverContract(observer))}
            .also { observer.contract(it) }

    override fun bindTo(root: MBindableMList<T>): Contract {
        val contract = Contract()

        if( root is BindableMList<T>) {
            contract.addContractor(BindContract())

            if( root.underlying != underlying) {
                val oldUnderlying = underlying
                val newUnderlying = root.underlying
                underlying = newUnderlying

                // have the New Underlying swallow all Old Underlyings and update all BindContracts
                newUnderlying.bindings.addAll(oldUnderlying.bindings)
                oldUnderlying.bindings.asSequence()
                    .flatMap { it.myTriggers }
                    .forEach {
                        it.elementsRemoved(oldUnderlying.list)
                        it.elementsAdded(0, newUnderlying.list)
                    }
                oldUnderlying.bindings.clear()
            }
        }
        else {
            underlying.list.clear()
            underlying.list.addAll(root.list)
            contract.addContractor(ExternalBindContract(root))
        }
        root.respondToBind(this, contract)
        return contract
    }

    override fun respondToBind(derived: MBindableMList<T>, contract: Contract) {
        if(derived !is BindableMList<T>)
            contract.addContractor(ExternalBindContract(derived))
    }

    private class Underlying<T>(col: Collection<T>, root: BindableMList<T>) {
        val list = ObservableMList(col)

        val externalTrigger = object: IListTriggers<T> {
            override fun elementsAdded(index: Int, elements: Collection<T>)
                {list.addAll(index,  elements)}
            override fun elementsRemoved(elements: Collection<T>)
                {list.removeAll(elements)}
            override fun elementsChanged(changes: Set<ListChange<T>>)
                {list.setMany(changes)}
            override fun elementsPermuted(permutation: ListPermuation)
                {list.permute(permutation)}
        }
        val internalTrigger = object : IListTriggers<T> {
            override fun elementsAdded(index: Int, elements: Collection<T>)
                {triggers.forEach { it.elementsAdded(index, elements) }}
            override fun elementsRemoved(elements: Collection<T>)
                {triggers.forEach { it.elementsRemoved(elements) }}
            override fun elementsChanged(changes: Set<ListChange<T>>)
                {triggers.forEach { it.elementsChanged(changes) }}
            override fun elementsPermuted(permutation: ListPermuation)
                {triggers.forEach { it.elementsPermuted(permutation) }}
        }

        init {list.addObserver(internalTrigger.observer())}

        val bindings = mutableSetOf(root)
        val triggers: Sequence<IListTriggers<T>>
            get() = bindings.asSequence().flatMap { it.myTriggers }
    }


    private inner class ExternalBindContract( val external: MBindableMList<T>)
        :IContractor
    {
        init {externalBinds.add(this)}

        override fun void() {externalBinds.remove(this)}
    }

    private inner class BindContract: IContractor
    {
        val bindable get() = this@BindableMList

        init {underlying.bindings.add(bindable)}

        override fun void() {
            underlying.bindings.remove(bindable)
            bindable.underlying = Underlying(underlying.list, bindable)
        }
    }

    private inner class ObserverContract(val observer: IMutableListObserver<T>)
        :IContractor
    {
        init {contracts.add(this)}

        override fun void() {contracts.remove(this)}
    }
}