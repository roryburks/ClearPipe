package clearpipe.ui.mainViews.center

import clearpipe.canvasFxDraws.draw
import clearpipe.model.imageData.AafAnimation
import clearpipe.model.master.IMasterControl
import javafx.scene.Parent
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import rb.extendo.delegates.OnChangeDelegate
import rb.jvm.javafx.valueBind
import rb.owl.bindable.addObserver
import rb.vectrix.mathUtil.MathUtil
import rb.vectrix.mathUtil.d
import rb.vectrix.mathUtil.floor
import rb.vectrix.mathUtil.i
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

        frameBind.addObserver { new, old -> drawView.met = new.floor }
    }

    private val _currAnumK = master.obs.currentAnimation.addObserver { new, _ ->
        sliderFrame.min = 0.0
        sliderFrame.max = new?.frames?.count()?.d ?: 1.0
        drawView.anim = new
    }
}

private val colorMap = mapOf(
    0 to Color.RED,
    1 to Color.BLACK,
    2 to Color.GRAY,
    3 to Color.CADETBLUE)

class HitboxDrawView : View() {
    var anim by OnChangeDelegate<AafAnimation?>(null) { recalcShift(it); redraw() }
    var met: Int by OnChangeDelegate(0){redraw()}
    val canvas = canvas(500.0,500.0) {}

    var shiftX = 0
    var shiftY = 0

    override val root= scrollpane {
        add(canvas)
        canvas.hgrow = Priority.ALWAYS
        canvas.vgrow = Priority.ALWAYS
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS
    }

    fun recalcShift(animation: AafAnimation?) {
        shiftX = -(animation?.x1 ?: 0)
        shiftY = -(animation?.y1 ?: 0)
    }

    fun redraw() {
        val gc = canvas.graphicsContext2D
        gc.clearRect(0.0, 0.0, canvas.width, canvas.height)

        val anim = anim ?: return
        anim.getDraws(met).forEach {
            gc.drawImage(it.image,
                it.area.x1, it.area.y1, it.area.w, it.area.h,
                it.offsetX + shiftX, it.offsetY + shiftY, it.area.w, it.area.h)
        }

        anim.getFrame(met).hboxes.forEach {
            val c = colorMap[it.typeId.i] ?: Color.WHITESMOKE

            gc.stroke = Color(c.red, c.green, c.blue, 0.7)
            gc.lineWidth = 1.5

            gc.fill = Color(c.red, c.green, c.blue, 1.0)
            it.col.draw(gc)
            gc.lineWidth = 1.0
        }
    }
}