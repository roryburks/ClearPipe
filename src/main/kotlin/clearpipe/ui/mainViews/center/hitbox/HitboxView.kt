package clearpipe.ui.mainViews.center.hitbox

import clearpipe.canvasFxDraws.draw
import clearpipe.canvasFxDraws.shift
import clearpipe.model.imageData.AafAnimation
import clearpipe.model.master.IMasterControl
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.input.KeyCode
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.transform.Affine
import rb.extendo.delegates.OnChangeDelegate
import rb.jvm.javafx.selectedBind
import rb.jvm.javafx.valueBind
import rb.owl.Observable
import rb.owl.addObserver
import rb.owl.bindable.addObserver
import rb.vectrix.mathUtil.MathUtil
import rb.vectrix.mathUtil.d
import rb.vectrix.mathUtil.floor
import rb.vectrix.mathUtil.i
import tornadofx.*

typealias HitboxTrigger = ()->Unit

class HitboxView(val master: IMasterControl) : View() {

    // Components
    val hitboxObservavle = Observable<HitboxTrigger>()
    val toolset = HitboxToolset()
    val penner : IHitboxPenner = HitboxPenner(toolset, hitboxObservavle)

    // UI
    val drawView = HitboxDrawView(penner, hitboxObservavle)
    val btnCopy = button("Copy to All")

    val sliderFrame = slider()
    val btnLeft = button("<")
    val btnRight = button(">")
    val listView = HitboxListView()
    val propertyView = HitboxPropertyView(hitboxObservavle)


    val metBind = sliderFrame.valueBind()
    var met get() = metBind.field
        set(value) {metBind.field = MathUtil.cycle(sliderFrame.min, sliderFrame.max, value)}

    override val root: Parent = vbox {
        spacing = 2.0
        hbox {
            add(drawView)
            vbox {
                add(listView)
                add(propertyView)
            }
        }

        hbox {
            spacing = 5.0
            add(HitboxToolsetView(toolset))
            add(btnCopy)
        }

        hbox {
            add(btnLeft)
            add(sliderFrame)
            add(btnRight)
        }
    }

    val curAnim get() = drawView.anim
    val curFrame get() = drawView.anim?.getFrame(drawView.met)

    init /* Bindings */ {
        btnLeft.setOnAction { met -= 1.0 }
        btnRight.setOnAction { met += 1.0 }
        btnCopy.setOnAction {evt->
            val cframe = curFrame ?: return@setOnAction
            val anim = curAnim ?: return@setOnAction

            anim.frames
                .filter { it != cframe }
                .forEach { frame ->
                    frame.hboxes.clear()
                    frame.hboxes.addAll(cframe.hboxes.map { it.copy() })
                }
        }

        metBind.addObserver { new, old ->
            drawView.met = new.floor
            penner.met = new.floor
            listView.frame = drawView.anim?.getFrame(new.floor)
        }

        hitboxObservavle.addObserver { listView.rebuild() }

        listView.listView.selectedBind().bindTo(penner.selectedBoxBind)
        penner.selectedBoxBind.addObserver { _, _ -> drawView.redraw()}
        propertyView.hitboxBind.bindTo(penner.selectedBoxBind)
    }

    private val _currAnumK = master.obs.currentAnimation.addObserver { new, _ ->
        sliderFrame.min = 0.0
        sliderFrame.max = new?.frames?.count()?.d ?: 1.0
        drawView.anim = new
        penner.animation = new
        listView.frame = drawView.anim?.getFrame(0)
    }
}

internal class HitboxToolsetView(val toolset: IHitboxToolset) : View() {
    private class ToolButton(val tool: HitboxTool) : Button(tool.btn)

    private val btns = HitboxTool.values().map { tool ->
        ToolButton(tool).also {
            it.tooltip =  Tooltip(tool.desc)
            it.setOnAction { toolset.tool = tool }
        }
    }

    override val root = hbox{
        btns.forEach { add(it) }
    }

    init {
        toolset.toolBind.addObserver { new, old ->
            btns.forEach {
                if( it.tool == new)
                    it.style = "-fx-background-color: #55AA55;"
                else
                    it.style = "-fx-background-color: #AAAAAA;"
            }
        }
    }
}

private val colorMap = mapOf(
    0 to Color.RED,
    1 to Color.BLACK,
    2 to Color.GRAY,
    3 to Color.CADETBLUE)

class HitboxDrawView(
    val penner: IHitboxPenner,
    val hitboxObservable: Observable<HitboxTrigger>)
    : View()
{
    var anim by OnChangeDelegate<AafAnimation?>(null) { recalcShift(it); redraw() }
    var met: Int by OnChangeDelegate(0){redraw()}
    val canvas = canvas(500.0,500.0) {}

    var shiftX = 0
    var shiftY = 0

    init {
        canvas.setOnMousePressed {
            canvas.requestFocus()
            penner.mouseDown(it.x - shiftX, it.y - shiftY, it.isShiftDown, it.isControlDown)
            if( penner.isDrawn)
                redraw()
        }
        canvas.setOnMouseDragged {
            canvas.requestFocus()
            penner.mouseDrag(it.x - shiftX, it.y - shiftY, it.isShiftDown, it.isControlDown)
            if( penner.isDrawn)
                redraw()
        }
        canvas.setOnMouseReleased {
            canvas.requestFocus()
            penner.mouseUp(it.x - shiftX, it.y - shiftY, it.isShiftDown, it.isControlDown)
            redraw()
        }


        canvas.setOnKeyPressed {
            when(it.code) {
                KeyCode.DELETE -> {
                    val anim = anim ?: return@setOnKeyPressed
                    val frame = anim.getFrame(met)
                    frame.hboxes.remove(penner.selectedBox)
                    hitboxObservable.trigger {it()}
                }
                KeyCode.UP -> if( it.isControlDown && it.isShiftDown) {
                    penner.selectedBox?.run { col = col.shift(0.0, -1.0)}
                    hitboxObservable.trigger {it()}
                }
                KeyCode.DOWN -> if( it.isControlDown && it.isShiftDown) {
                    penner.selectedBox?.run { col = col.shift(0.0, 1.0)}
                    hitboxObservable.trigger {it()}
                }
                KeyCode.LEFT -> if( it.isControlDown && it.isShiftDown) {
                    penner.selectedBox?.run { col = col.shift(-1.0, 0.0)}
                    hitboxObservable.trigger {it()}
                }
                KeyCode.RIGHT -> if( it.isControlDown && it.isShiftDown) {
                    penner.selectedBox?.run { col = col.shift(1.0, 0.0)}
                    hitboxObservable.trigger {it()}
                }
            }
        }
    }

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
        //gc.save()
        gc.clearRect(0.0, 0.0, canvas.width, canvas.height)
        gc.transform = Affine.affine(1.0,0.0,0.0,1.0,shiftX.d, shiftY.d)

        val anim = anim ?: return
        anim.getDraws(met).forEach {
            gc.drawImage(it.image,
                it.area.x1, it.area.y1, it.area.w, it.area.h,
                it.offsetX, it.offsetY, it.area.w, it.area.h)
        }

        anim.getFrame(met).hboxes.forEach {
            val c = colorMap[it.typeId.i] ?: Color.CYAN

            if( penner.selectedBox == it) {
                gc.fill = Color.TRANSPARENT
                val ci = c.invert()
                gc.stroke = Color(ci.red,ci.green,ci.blue,1.0)
                gc.lineWidth = 2.5
                it.col.draw(gc)
            }

            gc.lineWidth = 1.5
            gc.fill = Color(c.red, c.green, c.blue, 0.3)
            gc.stroke = Color(c.red, c.green, c.blue, 0.7)
            it.col.draw(gc)
            gc.lineWidth = 1.0
        }

        penner.draw(gc)
        //gc.restore()
        gc.transform = Affine()
    }
}