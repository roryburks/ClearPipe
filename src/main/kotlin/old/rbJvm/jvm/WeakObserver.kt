package old.rbJvm.jvm

import rb.extendo.dataStructures.SinglySequence
import rb.extendo.extensions.mapRemoveIfNull
import rb.global.IContract
import rb.owl.IObservable
import rb.owl.IObserver
import rb.owl.bindable.Bindable
import rb.owl.bindable.IBindObserver
import rb.owl.bindable.IBindable
import rb.owl.bindable.OnChangeEvent
import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

class WeakObserver<T>(trigger: T) : IObserver<T>
{
    private val weakTrigger = WeakReference(trigger)
    val trigger : T? =  weakTrigger.get()
    override val triggers: Sequence<T>? get() = trigger?.run { SinglySequence(this)}
}

/** Note: the contract strongly references the Trigger, so the trigger is preserved as long as the contract is. */
fun <T> IBindable<T>.addWeakObserver(t: (new: T, old: T)->Unit) : IContract = WeakObserverContract(this.addObserver(
    WeakObserver(t)
),t)

private class WeakObserverContract<T>(private val bindContract: IContract, val t: T) : IContract {
    override fun void() {bindContract.void()}
}

class WeakBindable<T>(default: T) : IObservable<OnChangeEvent<T>>
{
    val bind = Bindable(default)

    override fun addObserver(observer: IBindObserver<T>, trigger: Boolean): IContract = ObserverContract(observer)
    fun bindTo( root: Bindable<T>) : IContract = BindContract(root)

    private inner class BindContract( val externalBind: Bindable<T>) : IContract {
        init {bind.field = externalBind.field}
        val bindToWeakTrigger = externalBind.addWeakObserver { new, _ ->  bind.field = new}
        val weakToBindTrigger = bind.addWeakObserver { new, _ ->  externalBind.field = new}
        override fun void() {
            bindToWeakTrigger.void()
            weakToBindTrigger.void()
        }
    }
    
    private inner class ObserverContract(val observer: IBindObserver<T>) : IContract {
        init {observers.add(observer)}
        override fun void() {observers.remove(observer)}
    }

    private val binds = mutableListOf<BindContract>()
    private val triggers get() = observers.mapRemoveIfNull { it.triggers }
        .flatMap { it }
    private val observers = mutableListOf<IBindObserver<T>>()

    operator fun getValue(thisRef: Any, prop: KProperty<*>): T = bind.field
    operator fun setValue(thisRef:Any, prop: KProperty<*>, value: T) {bind.field = value}
}