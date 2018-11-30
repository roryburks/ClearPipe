package rb.owl.bindable

import rb.owl.Contract
import rb.owl.IObserver


typealias IBindObserver<T> = IObserver<OnChangeEvent<T>>

interface IBindable<T> {
    val field: T
    fun addObserver( observer: IBindObserver<T>, trigger : Boolean =  true ) : Contract
}

interface MBindable<T> : IBindable<T>{
    override var field: T
    fun bindTo( root: MBindable<T>) : Contract
    fun respondToBind( derived: MBindable<T>, contract: Contract)
    val triggers : Sequence<OnChangeEvent<T>>
}