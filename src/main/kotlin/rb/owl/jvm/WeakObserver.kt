package rb.owl.jvm

import rb.owl.Contract
import rb.owl.IContractor
import rb.owl.IObserver
import java.lang.ref.WeakReference

class WeakObserver<T>(trigger: T) : IObserver<T>
{
    private val weakTrigger = WeakReference(trigger)
    override val trigger get() = weakTrigger.get()

    override fun contract(contract: Contract) {}
}
