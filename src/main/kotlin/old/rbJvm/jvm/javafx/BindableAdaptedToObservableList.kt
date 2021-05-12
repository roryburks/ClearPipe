package rb.owl.jvm.javafx

import javafx.beans.InvalidationListener
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import rb.extendo.dataStructures.SinglySequence
import rb.owl.bindableMList.*


fun <T> BindableMList<T>.adaptToJFX() = BindAdaptedToObservableList(this)

abstract class BaseBindFxAdaptedObservableList<T>(val list: BindableMList<T>)
    : ObservableList<T>
{

    // region Delegated
    override fun contains(element: T) = list.list.contains(element)
    override fun addAll(vararg elements: T) = list.list.addAll(elements)
    override fun containsAll(elements: Collection<T>) = list.list.containsAll(elements)
    override fun get(index: Int) = list.list[index]
    override fun isEmpty() = list.list.isEmpty()
    override fun lastIndexOf(element: T) = list.list.lastIndexOf(element)
    override fun addAll(index: Int, elements: Collection<T>) = list.list.addAll(index, elements)
    override fun addAll(elements: Collection<T>) = list.list.addAll(elements)
    override fun clear() = list.list.clear()
    override fun listIterator() = list.list.listIterator()
    override fun listIterator(index: Int) = list.list.listIterator(index)
    override fun removeAll(vararg elements: T) = list.list.removeAll(elements)
    override val size: Int = list.list.size
    override fun indexOf(element: T) = list.list.indexOf(element)
    override fun iterator() = list.list.iterator()
    override fun add(element: T) = list.list.add(element)
    override fun add(index: Int, element: T) = list.list.add(index, element)
    override fun remove(element: T) = list.list.remove(element)
    override fun removeAll(elements: Collection<T>) = list.list.removeAll(elements)
    override fun removeAt(index: Int) = list.list.removeAt(index)
    override fun set(index: Int, element: T) = list.list.set(index, element)
    override fun retainAll(vararg elements: T) = list.list.retainAll(elements)
    override fun retainAll(elements: Collection<T>) = list.list.retainAll(elements)
    override fun subList(fromIndex: Int, toIndex: Int) = list.list.subList(fromIndex, toIndex)
    override fun setAll(vararg elements: T): Boolean {
        val any = list.list.any()
        list.list.clear()
        list.list.addAll(elements)
        return any
    }
    override fun setAll(col: MutableCollection<out T>?): Boolean {
        val any = list.list.any()
        list.list.clear()
        list.list.addAll(col ?: return any)
        return any
    }
    // endregion

    override fun remove(from: Int, to: Int) = list.list.subList(from, to).clear()

}

class BindAdaptedToObservableList<T>(list: BindableMList<T>)
    :BaseBindFxAdaptedObservableList<T>(list)
{
    private val backingList = FXCollections.observableArrayList<T>(list.list)

    val listeners = mutableListOf<ListChangeListener<in T>?>()
    override fun addListener(listener: ListChangeListener<in T>?) {
        listeners.add(listener)
        backingList.addListener(listener)
    }

    override fun removeListener(listener: ListChangeListener<in T>?) {
        backingList.removeListener(listener)
    }

    override fun addListener(listener: InvalidationListener?) {
        backingList.addListener(listener)
    }
    override fun removeListener(listener: InvalidationListener?) {
        backingList.removeListener(listener)
    }

    init {
        list.addObserver(object : IMutableListObserver<T> {
            val trigger: IListTriggers<T> = object : IListTriggers<T> {
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
            override val triggers get() = SinglySequence(trigger)
        })
    }

}