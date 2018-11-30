package rb.owl


interface IObserver<T> {
    val trigger: T?
    fun contract(contract: Contract)
}

interface IObservable<T> {
    fun addObserver( observer: IObserver<T>, trigger: Boolean = false) : Contract
}

class Observable<T> : IObservable<T>
{
    override fun addObserver(observer: IObserver<T>, trigger: Boolean): Contract {
        return Contract()
            .also { observers.add(MetaContract(observer)) }
            .also { observer.contract(it) }
    }

    fun trigger(lambda : (T)->Unit) {
        observers.forEach { it.observer.trigger?.apply(lambda) }
    }

    private val observers = mutableListOf<MetaContract>()

    private inner class MetaContract(val observer: IObserver<T>) : IContractor {
        override fun void() {observers.remove(this)}
    }
}