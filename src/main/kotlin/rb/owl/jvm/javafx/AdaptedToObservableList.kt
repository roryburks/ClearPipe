package rb.owl.jvm.javafx

import javafx.beans.InvalidationListener
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import rb.owl.bindableMList.ObservableMList

fun <T> ObservableMList<T>.adaptToJFX() = AdaptedToObservableList(this)

class AdaptedToObservableList<T>(val list: ObservableMList<T>)
    :ObservableList<T>
{
    // region Delegated
    override fun contains(element: T) = list.contains(element)
    override fun addAll(vararg elements: T) = list.addAll(elements)
    override fun containsAll(elements: Collection<T>) = list.containsAll(elements)
    override fun get(index: Int) = list[index]
    override fun isEmpty() = list.isEmpty()
    override fun lastIndexOf(element: T) = list.lastIndexOf(element)
    override fun addAll(index: Int, elements: Collection<T>) = list.addAll(index, elements)
    override fun addAll(elements: Collection<T>) = list.addAll(elements)
    override fun clear() = list.clear()
    override fun listIterator() = list.listIterator()
    override fun listIterator(index: Int) = list.listIterator(index)
    override fun removeAll(vararg elements: T) = list.removeAll(elements)
    override val size: Int = list.size
    override fun indexOf(element: T) = list.indexOf(element)
    override fun iterator() = list.iterator()
    override fun add(element: T) = list.add(element)
    override fun add(index: Int, element: T) = list.add(index, element)
    override fun remove(element: T) = list.remove(element)
    override fun removeAll(elements: Collection<T>) = list.removeAll(elements)
    override fun removeAt(index: Int) = list.removeAt(index)
    override fun set(index: Int, element: T) = list.set(index, element)
    override fun retainAll(vararg elements: T) = list.retainAll(elements)
    override fun retainAll(elements: Collection<T>) = list.retainAll(elements)
    override fun subList(fromIndex: Int, toIndex: Int) = list.subList(fromIndex, toIndex)

    override fun remove(from: Int, to: Int) = list.subList(from, to).clear()
    override fun setAll(vararg elements: T): Boolean {
        val any = list.any()
        list.clear()
        list.addAll(elements)
        return any
    }
    override fun setAll(col: MutableCollection<out T>?): Boolean {
        val any = list.any()
        list.clear()
        list.addAll(col ?: return any)
        return any
    }
    // endregion

    val fxListener = object: ListChangeListener<T> {
        override fun onChanged(c: ListChangeListener.Change<out T>) {
            while (c.next()) {
                when {
                    c.wasPermutated() -> {
                        for (old in c.from until c.to) {
                            val new = c.getPermutation(old)
                        }
                    }
                    c.wasUpdated() -> {
                        val updated = c.get

                    }
                    else -> {

                    }
                }
            }
        }

    }

    override fun addListener(listener: ListChangeListener<in T>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addListener(listener: InvalidationListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun removeListener(listener: ListChangeListener<in T>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeListener(listener: InvalidationListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}