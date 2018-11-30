package rb.owl.jvm

import rb.owl.Contract
import rb.owl.IContractor
import rb.owl.bindableMList.IMutableListObserver
import rb.owl.bindableMList.IMutableListTriggers
import java.lang.ref.WeakReference


class WeakMutableListObserver<T>(trigger: IMutableListTriggers<T>) :
    IMutableListObserver<T>
{

    private val weakTrigger = WeakReference(trigger)
    override val trigger = weakTrigger.get() ?: (NilTrigger<T>().also{ clearContracts() })

    override fun contract(contract: rb.owl.Contract) {
        val contractor = Contractor(contract)
        contract.addContractor(contractor)
    }


    private fun clearContracts() {
        contracts.forEach { it.void() }
        contracts.clear()
    }

    private var contracts = mutableListOf<Contractor>()
    private inner class Contractor(val contract: Contract) : IContractor {
        init { contracts.add(this)}

        override fun void() {
            contract.void()
            contracts.remove(this)
        }
    }
}

private class NilTrigger<T>: IMutableListTriggers<T> {
    override fun elementsAdded(inex: Int, elements: Collection<T>) {}
    override fun elementsRemoved(elements: Collection<T>) {}

}