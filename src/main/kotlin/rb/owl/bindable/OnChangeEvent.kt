package rb.owl.bindable

import rb.owl.Contract

typealias OnChangeEvent<T> = (new: T, old:T)->Unit

fun <T> onChangeObserver(trigger: (new:T, old:T)->Unit ) = object : IBindObserver<T> {
    override val trigger: OnChangeEvent<T> = trigger
    override fun contract(contract: Contract) {}
}

fun <T> IBindable<T>.addObserver(trigger: Boolean = true, event: (new: T, old: T) -> Unit)
        = addObserver(onChangeObserver<T>(event), trigger)