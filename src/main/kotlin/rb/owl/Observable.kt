package rb.owl


interface IObserver<T> {
    val trigger: T?
    fun contract(contract: Contract)
}

class Observer<T>(override val trigger: T) : IObserver<T>{
    override fun contract(contract: Contract) {}
}
fun <T> T.observer() = Observer(this)

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
        observers.removeIf { it.observer.trigger?.apply(lambda) == null }
    }

    private val observers = mutableListOf<MetaContract>()

    private inner class MetaContract(val observer: IObserver<T>) : IContractor {
        override fun void() {observers.remove(this)}
    }
}