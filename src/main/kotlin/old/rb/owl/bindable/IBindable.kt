package old.rb.owl.bindable

import old.rb.owl.IObservable
import old.rb.owl.IObserver


typealias IBindObserver<T> = IObserver<OnChangeEvent<T>>

interface IBindable<T> : IObservable<OnChangeEvent<T>> {
    val field: T
}