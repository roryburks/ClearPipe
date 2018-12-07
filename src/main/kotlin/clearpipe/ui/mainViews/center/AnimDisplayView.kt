package clearpipe.ui.mainViews

import clearpipe.model.imageData.AafAnimation
import clearpipe.model.master.IMasterControl
import javafx.animation.AnimationTimer
import javafx.animation.Timeline
import javafx.scene.Parent
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import rb.extendo.delegates.OnChangeDelegate
import rb.owl.bindable.addObserver
import rb.owl.jvm.addWeakObserver
import rb.vectrix.mathUtil.d
import rb.vectrix.mathUtil.floor
import rb.vectrix.mathUtil.round
import tornadofx.*
import java.util.*

class AnimDisplayView(val master: IMasterControl) : View() {
    private val draw = AnimDrawView()
    val animLabel = label()
    val fpsTextField = textfield("8")
    override val root = vbox {
        add(draw)
        hbox {
            togglebutton("Play",selectFirst = false) {
                setOnAction { play = this.isSelected }
            }
            add(animLabel)
            add(fpsTextField)

        }
    }

    init {
        master.obs.currentAnimation.addObserver { new, old ->
            draw.anim = new
            animLabel.text = new?.name ?: ""
        }
        fpsTextField.textProperty().addListener { observable, oldValue, newValue ->
            fps = newValue.toDoubleOrNull() ?: fps
        }
    }

    init {
        val t = Timer()
        t.scheduleAtFixedRate(object : TimerTask(){
            override fun run() {
                if( play)
                    met += fps / 50.0
            }
        }, 0, 20)
    }

    var play = false
    var fps: Double = 8.0
    var met: Double = 0.0
        set(value) {
            field = value
            draw.frame = value.floor
        }
}

private class AnimDrawView() : View() {
    var anim by OnChangeDelegate<AafAnimation?>(null) { redraw()}
    var frame: Int by OnChangeDelegate(0){redraw()}

    val canvas = canvas(500.0,500.0) {
    }

    var ox = 0
    var oy = 0

    override val root= scrollpane {
        add(canvas)
        canvas.hgrow = Priority.ALWAYS
        canvas.vgrow = Priority.ALWAYS
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS

        canvas.setOnMousePressed {
            ox = it.x.round
            oy = it.y.round
            redraw()
        }
        canvas.setOnMouseDragged {
            ox = it.x.round
            oy = it.y.round
            redraw()
        }
    }

    fun redraw() {
        val gc = canvas.graphicsContext2D
        gc.clearRect(0.0, 0.0, canvas.width, canvas.height)

        val anim = anim ?: return
        anim.getDraws(frame).forEach {
            gc.drawImage(it.image,
                it.area.x1, it.area.y1, it.area.w, it.area.h,
                it.offsetX, it.offsetY, it.area.w, it.area.h)
        }

        gc.stroke = Color(0.1, 0.3, 0.1, 0.7)
        gc.strokeLine(ox.d - 3, oy.d, ox.d + 3, oy.d)
        gc.strokeLine(ox.d , oy.d- 3, ox.d, oy.d + 3)
        gc.stroke = Color(0.4, 0.9, 0.4, 0.7)
        gc.strokeLine(ox.d - 3, oy.d+1, ox.d + 3, oy.d+1)
        gc.strokeLine(ox.d-1 , oy.d- 3, ox.d-1, oy.d + 3)

    }
}