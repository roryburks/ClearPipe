package clearpipe.ui.mainViews.center

import clearpipe.model.imageData.AafAnimation
import clearpipe.model.master.IMasterControl
import javafx.scene.Parent
import javafx.scene.layout.Priority
import rb.extendo.delegates.OnChangeDelegate
import rb.jvm.javafx.valueBind
import rb.owl.bindable.addObserver
import rb.vectrix.mathUtil.MathUtil
import rb.vectrix.mathUtil.d
import rb.vectrix.mathUtil.floor
import tornadofx.*


class HitboxView(val master: IMasterControl) : View() {
    val drawView = HitboxDrawView()
    val sliderFrame = slider()
    val btnLeft = button("<")
    val btnRight = button(">")


    val frameBind = sliderFrame.valueBind()
    var frame get() = frameBind.field
        set(value) {frameBind.field = MathUtil.cycle(sliderFrame.min, sliderFrame.max, value)}

    override val root: Parent = vbox {
        add(drawView)

        hbox {
            add(btnLeft)
            add(sliderFrame)
            add(btnRight)
        }
    }

    init /* Bindings */ {
        btnLeft.setOnAction { frame -= 1.0 }
        btnRight.setOnAction { frame += 1.0 }

        frameBind.addObserver { new, old -> drawView.frame = new.floor }
    }

    private val _currAnumK = master.obs.currentAnimation.addObserver { new, _ ->
        sliderFrame.min = 0.0
        sliderFrame.max = new?.frames?.count()?.d ?: 1.0
        drawView.anim = new
    }
}

class HitboxDrawView : View() {
    var anim by OnChangeDelegate<AafAnimation?>(null) { redraw()}
    var frame: Int by OnChangeDelegate(0){redraw()}
    val canvas = canvas(500.0,500.0) {}

    override val root= scrollpane {
        add(canvas)
        canvas.hgrow = Priority.ALWAYS
        canvas.vgrow = Priority.ALWAYS
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS
    }

    fun redraw() {


    }
}