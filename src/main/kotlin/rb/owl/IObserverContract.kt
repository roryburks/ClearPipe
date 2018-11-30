package rb.owl

class Contract {
    private val contractors = mutableListOf<IContractor>()
    fun void() = contractors.forEach { it.void() }
    fun addContractor( contactor : IContractor) {
        contractors.add(contactor)
    }
}

interface IContractor {
    fun void()
}

object NilContractor : IContractor {
    override fun void() {}
}
