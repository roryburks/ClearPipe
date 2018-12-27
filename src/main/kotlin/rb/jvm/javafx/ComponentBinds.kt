package rb.jvm.javafx

import clearpipe.ui.mainViews.addValueListener
import javafx.scene.control.Slider
import rb.owl.bindable.Bindable
import rb.owl.bindable.IBindable
import rb.owl.bindable.addObserver

fun Slider.valueBind() : Bindable<Double> {
    val bindable = Bindable(this.value)
    this.addValueListener { bindable.field = it }
    bindable.addObserver { new, _ -> this.value = new }
    return bindable
}