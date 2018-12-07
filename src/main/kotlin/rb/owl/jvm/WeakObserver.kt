package rb.owl.jvm

import rb.owl.Contract
import rb.owl.IContractor
import rb.owl.IObserver
import rb.owl.bindable.IBindable
import java.lang.ref.WeakReference

class WeakObserver<T>(trigger: T) : IObserver<T>
{
    val xyz = trigger.toString()
    private val weakTrigger = WeakReference(trigger)
    override val trigger : T? get() {
        val x = weakTrigger.get()
        if( x == null) println("fallen out: $xyz")
        return x
    }

        override fun contract(contract: Contract) {}
}

fun <T> T.weakObserver() = WeakObserver(this)

fun <T> IBindable<T>.addWeakObserver(t: (new: T, old: T)->Unit) = t.also {
    println(t)
    this.addObserver(WeakObserver(t))}