package clearpipe.ui.mainViews

import clearpipe.model.draw
import clearpipe.model.animation.AafAnimationK
import clearpipe.model.master.IMasterControl
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.canvas.Canvas
import javafx.scene.control.Slider
import javafx.scene.layout.Priority
import javafx.scene.transform.Affine
import javafx.scene.transform.Scale
import rb.extendo.delegates.OnChangeDelegate
import rb.owl.bindable.Bindable
import old.rbJvm.jvm.addWeakObserver
import rb.vectrix.mathUtil.d
import rb.vectrix.mathUtil.floor
import tornadofx.*
import kotlin.math.pow

class FrameOverlayView(
    val master: IMasterControl,
    val anim: AafAnimationK)
    : View()
{
    val frame = FrameCanvas(anim)
    val scaleSlider = Slider(0.0,8.0,5.0).also { it.orientation = Orientation.VERTICAL }
    val frameSlider = Slider(0.0, anim.frames.size.d, 0.0).also{ it.orientation = Orientation.HORIZONTAL}

    override val root: Parent = borderpane {
        frame.redraw()
        left { add(frame.also { it.hgrow = Priority.ALWAYS }) }
        right {
            add(scaleSlider.also { vgrow = Priority.ALWAYS })
            vgrow = Priority.ALWAYS
        }
        bottom {
            add(frameSlider)
        }
    }

    init {
        scaleSlider.addValueListener { frame.scale = it }
        frameSlider.addValueListener { frame.frame = it.floor}
    }
}

class FrameCanvas(val anim: AafAnimationK)
    : Canvas(200.0,200.0)
{
    var frame by OnChangeDelegate(0) {redraw()}
    var scale: Double by OnChangeDelegate(5.0) {redraw()}

    fun redraw() {
        val gc = graphicsContext2D
        gc.clearRect(0.0, 0.0, width, height)

        val scaleRatio = 2.0.pow(scale-5.0)
        gc.transform = Scale(scaleRatio, scaleRatio).run { Affine(mxx, mxy, mxz, tx, myx, myy, myz, ty, mzx, mzy, mzz, tz)}
        anim.getDraws(frame).forEach {it.draw(gc)}
        gc.transform = Affine(1.0,0.0, 0.0, 0.0, 1.0, 0.0)
    }
}

val Slider.valueBind get() : Bindable<Double> {
    val bind = Bindable(this.value)
    this.valueProperty().addListener { observable, oldValue, newValue ->
        bind.field = newValue.toDouble()
    }
    bind.addWeakObserver { new, old -> value = new }
    return bind
}


fun Slider.addValueListener(listener: (Double)->Unit) =
    valueProperty().addListener { _, _, newValue -> listener(newValue.toDouble()) }