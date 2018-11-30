package rb.owl.bindable

import rb.extendo.dataStructures.SinglySequence
import rb.extendo.extensions.mapRemoveIfNull
import rb.extendo.extensions.then
import rb.owl.Contract
import rb.owl.IContractor
import rb.owl.IObserver

// TODO: Support Unbind, WeakBind (or rather WeakBindable)


class Bindable<T>(default: T)
    : MBindable<T>
{
    private var underlying = Underlying(default, this)
    override var field: T
        get() = underlying.value
        set(value) {underlying.value = value}

    private val contracts = mutableListOf<ObserverContract>()
    private val externalBinds = mutableListOf<ExternalBindContract>()

    private val myTriggers: Sequence<OnChangeEvent<T>>
        get() = contracts.mapRemoveIfNull { it.observer.trigger }
            .then(externalBinds.asSequence().flatMap { it.external.triggers })



    override val triggers: Sequence<OnChangeEvent<T>> get() = underlying.triggers

    override fun addObserver(observer: IBindObserver<T>, trigger: Boolean): Contract {
        return Contract()
            .also { it.addContractor(ObserverContract(observer)) }
            .also { observer.contract(it) }
    }


    override fun bindTo(root: MBindable<T>): Contract {
        val contract = Contract()

        if( root is Bindable<T>) {
            contract.addContractor(BindContract())

            if( root.underlying != underlying) {
                val oldUnderlying = underlying
                val newUnderlying = root.underlying
                underlying = newUnderlying

                // have the New Underlying swallow all Old Underlyings and update all BindContracts
                newUnderlying.bindings.addAll(oldUnderlying.bindings)
                val oldValue = oldUnderlying.value
                val newValue = newUnderlying.value
                if (oldValue != newValue) {
                    oldUnderlying.bindings.asSequence()
                        .flatMap { it.myTriggers }
                        .forEach { it.invoke(newValue, oldValue) }
                }
                oldUnderlying.bindings.clear()

            }
        }
        else {
            underlying.value = root.field
            contract.addContractor(ExternalBindContract(root))
        }
        root.respondToBind(this, contract)

        return Contract()
    }

    override fun respondToBind(derived: MBindable<T>, contract: Contract) {
        if( derived !is Bindable<T>)
            contract.addContractor(ExternalBindContract(derived))
    }


    private class Underlying<T>( default: T, root: Bindable<T>) {
        var value: T = default
            set(value) {
                val prev = field
                if( value != field) {
                    field = value
                    triggers.forEach { it.invoke(value,prev)}
                }
            }

        val triggers : Sequence<OnChangeEvent<T>>
            get() = bindings.asSequence().flatMap { it.myTriggers }.then(onChange)
        val onChange = SinglySequence{new: T, _: T-> value = new}
        val bindings = mutableSetOf(root)   // Set avoids double-binding
    }

    private inner class ExternalBindContract(val external: MBindable<T>)
        :IContractor
    {
        init {externalBinds.add(this)}

        override fun void() {externalBinds.remove(this)}
    }

    private inner class BindContract :IContractor
    {
        val bindable get() = this@Bindable

        init {underlying.bindings.add(bindable)}

        override fun void() {
            underlying.bindings.remove(bindable)
            bindable.underlying = Underlying(underlying.value, bindable)
        }
    }


    private inner class ObserverContract(val observer: IBindObserver<T>)
        :IContractor
    {
        init {contracts.add(this)}

        override fun void() {
            contracts.remove(this)
        }
    }
}