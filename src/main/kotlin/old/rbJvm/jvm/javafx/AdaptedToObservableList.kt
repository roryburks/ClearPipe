package rb.owl.jvm.javafx

import javafx.beans.InvalidationListener
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import old.rb.owl.bindableMList.*

fun <T> ObservableMList<T>.adaptToJFX() = AdaptedToObservableList(this)

abstract class BaseFxAdaptedObservableList<T>(val list: ObservableMList<T>)
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

    override fun remove(from: Int, to: Int) = list.subList(from, to).clear()

}

class AdaptedToObservableList<T>(list: ObservableMList<T>)
    :BaseFxAdaptedObservableList<T>(list)
{
    private val backingList = FXCollections.observableArrayList<T>(list)

    override fun addListener(listener: ListChangeListener<in T>?) {
        backingList.addListener(listener)
    }

    override fun removeListener(listener: ListChangeListener<in T>?) {
        backingList.removeListener(listener)
    }

    override fun addListener(listener: InvalidationListener?) {}
    override fun removeListener(listener: InvalidationListener?) {}

    init {
        list.addObserver(object : IMutableListObserver<T> {
            override val trigger: IListTriggers<T> = object : IListTriggers<T> {
                override fun elementsAdded(index: Int, elements: Collection<T>) {
                    backingList.addAll(index, elements)
                }
                override fun elementsRemoved(elements: Collection<T>) {
                    backingList.removeAll(elements)
                }
                override fun elementsChanged(changes: Set<ListChange<T>>) {
                    changes.forEach { backingList[it.index] = it.new}
                }
                override fun elementsPermuted(permutation: ListPermuation) {
                    val old = (permutation.startIndex until permutation.endIndex)
                        .map { backingList[it] }
                    for (it in permutation.startIndex until permutation.endIndex) {
                        backingList[permutation[it]] = old[it]
                    }
                }
            }
        })
    }

}