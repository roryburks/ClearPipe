package old.rbJvm.jvm.javafx

import clearpipev1.ui.mainViews.addValueListener
import javafx.scene.control.ListView
import javafx.scene.control.Slider
import javafx.scene.control.TextField
import rb.owl.bindable.Bindable
import rb.owl.bindable.addObserver

fun Slider.valueBind() : Bindable<Double> {
    val bindable = Bindable(this.value)
    this.addValueListener { bindable.field = it }
    bindable.addObserver { new, _ -> this.value = new }
    return bindable
}

fun <T> ListView<T>.selectedBind() : Bindable<T?> {
    val bindable = Bindable(this.selectionModel.selectedItem)
    selectionModel.selectedItemProperty().addListener { _, _, new -> bindable.field = new }
    bindable.addObserver { new, _ -> selectionModel.select(new) }
    return bindable
}

fun TextField.intBind() : Bindable<Int> {
    val bindable = Bindable( this.text.toIntOrNull() ?: 0.also { this.text = "0" })

    textProperty().addListener { observable, oldValue, newValue ->
        val textAsInt = newValue.toIntOrNull()
        when {
            newValue == "" -> {}
            textAsInt == null -> {}
            else -> bindable.field = textAsInt
        }
    }
    bindable.addObserver { new, _ -> text = new.toString() }
    return bindable
}