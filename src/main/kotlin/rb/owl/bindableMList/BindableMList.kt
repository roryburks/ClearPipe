package rb.owl.bindableMList

import rb.extendo.dataStructures.SinglySequence
import rb.extendo.extensions.mapRemoveIfNull
import rb.extendo.extensions.then
import rb.owl.Contract
import rb.owl.IContractor
import rb.owl.bindable.Bindable


interface IBindableMList<T> {
    val list: List<T>
    fun addObserver( observer: IMutableListObserver<T>, trigger: Boolean = false) : Contract
}

interface MBindableMList<T> : IBindableMList<T> {
    override val list: MutableList<T>
    fun bindTo( root: MBindableMList<T>) : Contract
    fun respondToBind( derived: MBindableMList<T>, contract: Contract)
    val triggers: Sequence<IMutableListTriggers<T>>
}

class BindableMList<T>(list: Collection<T> = emptyList()) :
    MBindableMList<T>
{
    private var underlying = Underlying(list, this)
    override val list : MutableList<T> get() = underlying.list

    private val externalBinds = mutableListOf<ExternalBindContract>()
    private val contracts = mutableListOf<ObserverContract>()

    private val myTriggers : Sequence<IMutableListTriggers<T>>
        get() = contracts.mapRemoveIfNull { it.observer.trigger }
            .then(externalBinds.asSequence().flatMap { it.external.triggers })

    override val triggers: Sequence<IMutableListTriggers<T>> get() = underlying.triggers
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

        val externalTrigger = object: IMutableListTriggers<T> {
            override fun elementsAdded(inex: Int, elements: Collection<T>)
                {list.addAll(inex,  elements)}
            override fun elementsRemoved(elements: Collection<T>)
                {list.removeAll(elements)}
        }
        val internalTrigger = object : IMutableListTriggers<T> {
            override fun elementsAdded(inex: Int, elements: Collection<T>)
                {triggers.forEach { it.elementsAdded(inex, elements) }}
            override fun elementsRemoved(elements: Collection<T>)
                {triggers.forEach { it.elementsRemoved(elements) }}
        }

        init {list.addObserver(internalTrigger.observer())}

        val bindings = mutableSetOf(root)
        val triggers: Sequence<IMutableListTriggers<T>>
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