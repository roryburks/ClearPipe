package unitTest.rb.owl

import org.junit.Test
import old.rb.owl.bindable.Bindable
import old.rb.owl.bindable.onChangeObserver
import old.rb.owl.bindableMList.*
import kotlin.test.assertEquals


class ObservableMListTests {
    @Test
    fun Observe() {
        val list = ObservableMList<Double>()

        var sum = 0.0

        val observer = object : IListTriggers<Double> {
            override fun elementsAdded(inex: Int, elements: Collection<Double>) {
                elements.forEach{ sum += it}
            }
            override fun elementsRemoved(elements: Collection<Double>) {
                elements.forEach { sum -= it }
            }

            override fun elementsChanged(changes: Set<ListChange<Double>>) {
                TODO("Not yet implemented")
            }

            override fun elementsPermuted(permutation: ListPermuation) {
                TODO("Not yet implemented")
            }
        }.observer()
        val contract = list.addObserver(observer)

        // Make sure it's traking
        list.add(10.0)
        assert(sum == 10.0)

        // Make sure it tracks more than 1
        list.add(5.0)
        list.add(7.0)
        assert(sum == 22.0)

        // Make sure it tracks removals
        list.remove(5.0)
        assert(sum == 17.0)

        // Make sure it stops tracking once unbound
        contract.void()
        list.clear()
        assert(sum == 17.0)

        // Make sure it re-tracks once re-bound (ignoring mutations in between)
        list.add(1.0)
        list.add(2.0)
        list.add(3.0)
        list.add(4.0)
        list.addObserver(observer)
        list.clear()
        assert(sum == 7.0)
    }

    @Test fun BindableListTest()
    {
        val bindable1 = BindableMList<Double>()
        val bindable2 = BindableMList<Double>()
        var sum = 0.0
        val observer = object : IListTriggers<Double> {
            override fun elementsAdded(inex: Int, elements: Collection<Double>) {elements.forEach { sum += it }}
            override fun elementsRemoved(elements: Collection<Double>) {elements.forEach { sum -= it }}
            override fun elementsChanged(changes: Set<ListChange<Double>>) {
                TODO("Not yet implemented")
            }

            override fun elementsPermuted(permutation: ListPermuation) {
                TODO("Not yet implemented")
            }
        }.observer()
        val contract = bindable2.addObserver(observer)

        bindable1.list.add(100.0)
        bindable1.list.add(200.0)
        bindable1.list.add(300.0)
        bindable2.list.add(1.0)
        bindable2.list.add(2.0)
        bindable2.list.add(3.0)
        assertEquals(6.0, sum)

        bindable2.bindTo(bindable1)
        assertEquals(600.0, sum)

        bindable2.list.add(6.0)
        assertEquals(606.0, sum)

        bindable1.list.clear()
        assertEquals(0.0, sum)

        contract.void()
        bindable1.list.add(155.0)
        assertEquals(0.0, sum)
    }

    @Test fun BindableTest()
    {
        val bindable1 = Bindable(0.0)
        val bindable2 = Bindable(0.0)
        var sum = 0.0
        val observer = onChangeObserver<Double> {new, _ ->  sum += new}
        val contract = bindable2.addObserver(observer)

        bindable1.field = 100.0
        bindable1.field = 200.0
        bindable1.field = 300.0
        bindable2.field = 1.0
        bindable2.field = 2.0
        bindable2.field = 3.0
        assertEquals(6.0, sum)

        bindable2.bindTo(bindable1)
        assertEquals(306.0, sum)

        bindable2.field = 6.0
        assertEquals(312.0, sum)

        contract.void()
        bindable1.field = -999.9
        assertEquals(312.0, sum)
    }
}