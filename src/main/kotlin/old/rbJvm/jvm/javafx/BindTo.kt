package rb.owl.jvm.javafx

import javafx.collections.ObservableList
import rb.owl.bindableMList.*

fun <T> ObservableList<T>.bindTo(mlist: IBindableMList<T>) {
    val olist = this
    mlist.addObserver(MutableListObserver(object: IListTriggers<T> {
        override fun elementsAdded(index: Int, elements: Collection<T>) {
            olist.addAll(index, elements)
        }

        override fun elementsRemoved(elements: Collection<T>) {
            olist.removeAll(elements)
        }

        override fun elementsChanged(changes: Set<ListChange<T>>) {
            changes.forEach { olist[it.index] = it.new}
        }

        override fun elementsPermuted(permutation: ListPermuation) {
            val old = (permutation.startIndex until permutation.endIndex)
                .map { olist[it] }
            for (it in permutation.startIndex until permutation.endIndex) {
                olist[permutation[it]] = old[it]
            }
        }

    }))
}