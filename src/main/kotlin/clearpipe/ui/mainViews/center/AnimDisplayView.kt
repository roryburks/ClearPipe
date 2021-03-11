package clearpipe.ui.mainViews.center

import clearpipe.model.master.IMasterControl
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import old.rb.extendo.delegates.OnChangeDelegate
import old.rbJvm.jvm.javafx.intBind
import rb.owl.bindable.Bindable
import rb.owl.bindable.addObserver
import old.rb.vectrix.mathUtil.d
import old.rb.vectrix.mathUtil.floor
import old.rb.vectrix.mathUtil.round
import tornadofx.*
import java.util.*

class AnimDisplayView(val master: IMasterControl) : View() {
    private val controller = AnimDisplayController(master)
    private val draw = AnimDrawView(master, controller)

    // region UI
    val animLabel = label()
    val tfFps = textfield("8")
    val tfOx = textfield("0")
    val tfOy = textfield("0")
    override val root = vbox {
        add(draw)
        hbox {
            togglebutton("Play",selectFirst = false) {
                setOnAction { play = this.isSelected }
            }
            add(animLabel)
            add(tfFps)
            add(label("OX"))
            add(tfOx)
            add(label("OY"))
            add(tfOy)

        }
    }
    // endregion

    init {
        controller.animBind.addObserver { new, _ -> animLabel.text = new?.name ?: "" }
        tfFps.textProperty().addListener { _, _, newValue ->
            controller.fps = newValue.toDoubleOrNull() ?: controller.fps
        }

        tfOx.intBind().bindTo(controller.oxBind)
        tfOy.intBind().bindTo(controller.oyBind)

    }

    init {
        val t = Timer()
        t.scheduleAtFixedRate(object : TimerTask(){
            override fun run() {
                if( play)
                    controller
                    controller.met += controller.fps / 50.0
            }
        }, 0, 20)
    }

    var play = false
}

class AnimDisplayController( val master: IMasterControl) : Controller()
{
    var oxBind = Bindable(0)
    var oyBind = Bindable(0)
    var animBind = master.obs.currentAnimation
    var metBind = Bindable(0.0)

    var ox by oxBind
    var oy by oyBind
    val anim get() = animBind.field
    var met by metBind
    var fps = 8.0

    init {
        animBind.addObserver { new, _ ->
            ox = new?.ox ?: 0
            oy = new?.oy ?: 0
        }
        oxBind.addObserver { new, _ -> anim?.ox = new }
        oyBind.addObserver { new, _ -> anim?.oy = new }
    }
}

private class AnimDrawView(
    val master: IMasterControl,
    val controller: AnimDisplayController) : View() {
    init {
        controller.animBind.addObserver { _, _ -> redraw() }
        controller.oxBind.addObserver { _, _ -> redraw() }
        controller.oyBind.addObserver { _, _ -> redraw() }
        controller.metBind.addObserver { new, _ -> localMet = new.floor }
    }

    var localMet by OnChangeDelegate(0) {redraw()}
    val canvas = canvas(500.0,500.0) {}

    override val root= scrollpane {
        add(canvas)
        canvas.hgrow = Priority.ALWAYS
        canvas.vgrow = Priority.ALWAYS
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS

        fun setOrig(x: Int, y: Int, many: Boolean) {
            if( many)
                master.projectSet.current?.animations?.forEach {it.ox = x ; it.oy = y}
            else
                controller.anim?.also {  it.ox = x ; it.oy = y }

        }

        canvas.setOnMousePressed {
            setOrig(it.x.round, it.y.round,it.isAltDown && it.isShiftDown)
            redraw()
        }
        canvas.setOnMouseDragged {
            setOrig(it.x.round, it.y.round,it.isAltDown && it.isShiftDown)
            redraw()
            controller.ox = it.x.round
            controller.oy = it.y.round
        }
    }

    fun redraw() {
        val gc = canvas.graphicsContext2D
        gc.clearRect(0.0, 0.0, canvas.width, canvas.height)

        val anim = controller.anim ?: return
        val ox = controller.ox
        val oy = controller.oy
        anim.getDraws(localMet).forEach {
            gc.drawImage(it.image,
                it.area.x1, it.area.y1, it.area.w, it.area.h,
                it.offsetX + ox, it.offsetY + oy, it.area.w, it.area.h)
        }

        gc.stroke = Color(0.1, 0.3, 0.1, 0.7)
        gc.strokeLine(ox.d - 3, oy.d, ox.d + 3, oy.d)
        gc.strokeLine(ox.d , oy.d- 3, ox.d, oy.d + 3)
        gc.stroke = Color(0.4, 0.9, 0.4, 0.7)
        gc.strokeLine(ox.d - 3, oy.d+1, ox.d + 3, oy.d+1)
        gc.strokeLine(ox.d-1 , oy.d- 3, ox.d-1, oy.d + 3)

    }
}