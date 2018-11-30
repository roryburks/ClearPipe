package demos.rb.owl

import java.lang.ref.WeakReference

class Referer<T>(t: T ) {
    private val weakT = WeakReference(t)
    val t : T? get() = weakT.get()
}

class WeakReferenceTest {

    //@Test
    fun lambdaDoesntFall() {
        val referer = Referer { new: Double -> println(new) }

        repeat(10) {
            // Some reference is implied somewhere
            System.gc()
            referer.t?.invoke(10.0)
            Thread.sleep(300)
        }
    }

    class A(val a: Int, val b: Int, val c: Int)

    //@Test
    fun classFallsOut() {
        val referer = Referer(A(1, 2, 3))

        repeat(10) {
            // Will fall out and start printing out null
            System.gc()
            println(referer.t)
            Thread.sleep(300)
        }
    }


    class C(t: (Double)->Unit) {
        val x = t
    }
    //@Test
    fun lambdaFallsOut() {
        val referer = Referer(C { new: Double -> println(new) })

        repeat(10) {
            System.gc()
            referer.t?.x?.invoke(10.0)
            Thread.sleep(300)
        }
    }
}
