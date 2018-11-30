package rb.owl.bindableMList

import rb.owl.*

interface IMutableListTriggers<T> {
    fun elementsAdded(inex: Int, elements: Collection<T>)
    fun elementsRemoved(elements: Collection<T>)
}


typealias IMutableListObserver<T> = IObserver<IMutableListTriggers<T>>
typealias IMutableListObservable<T> = IObservable<IMutableListTriggers<T>>

fun <T> IMutableListTriggers<T>.observer() = MutableListObserver(this)

class MutableListObserver<T>(override val trigger: IMutableListTriggers<T>) :
    IMutableListObserver<T>
{
    override fun contract(contract: Contract) {}
}
