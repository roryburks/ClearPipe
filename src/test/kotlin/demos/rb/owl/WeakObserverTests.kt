package demos.rb.owl

import rb.owl.bindable.Bindable
import rb.owl.bindable.OnChangeEvent
import rb.owl.bindableMList.BindableMList
import rb.owl.bindableMList.IListTriggers
import rb.owl.jvm.WeakObserver

class WeakObserverTests {
    //@Test
    fun WeakBindableTest()
    {
        var sum = 0.0
        val bindable = Bindable(5.0)
        A().doThingToBindable(bindable)

        var list = mutableListOf<Double>()

        repeat(100)  {
            repeat(1000) {list.add(10.0)}
            System.gc()
            Runtime.getRuntime().gc()
            Thread.sleep(300)
            bindable.field++
        }
    }

    //@Test
    fun WeakBindableListTest()
    {
        var sum = 0.0
        val bindable = Bindable(5.0)
        val bindableList = BindableMList<Double>()
        B().doThingToBindable(bindableList)

        var list = mutableListOf<Double>()
        bindableList.list.add(1.0)

        repeat(100)  {
            repeat(1000) {list.add(10.0)}
            System.gc()
            Runtime.getRuntime().gc()
            Thread.sleep(300)
            bindable.field++
            bindableList.list.add(1.0)
        }

    }
    class B() {
        fun doThingToBindable(bindable: BindableMList<Double>) {
            val x = object: IListTriggers<Double> {
                override fun elementsAdded(inex: Int, elements: Collection<Double>) {}
                override fun elementsRemoved(elements: Collection<Double>) {}
            }
            val weakObservable = WeakObserver<IListTriggers<Double>>(x)
            bindable.addObserver(weakObservable)
        }

    }

    class A() {

        fun doThingToBindable(bindable: Bindable<Double>) {
            val weakObservable = WeakObserver{ new: Double, old: Double -> println("new")}
            bindable.addObserver(weakObservable)

        }
    }

    class Referent(val a: Int, val b: Int, val c:Int, val trigger: OnChangeEvent<Double>)
}