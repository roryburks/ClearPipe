package clearpipe.ui.mainViews.center.hitbox

import clearpipe.model.imageData.AafAnimation
import clearpipe.model.imageData.AafHitbox
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import rb.extendo.delegates.OnChangeDelegate
import rb.owl.bindable.Bindable
import rb.vectrix.intersect.CollisionRigidRect
import rb.vectrix.mathUtil.s
import rb.vectrix.shapes.RectD

interface IHitboxPenner
{
    var animation : AafAnimation?
    var met: Int
    val selectedBoxBind : Bindable<AafHitbox?>
    var selectedBox : AafHitbox?

    fun mouseDown(x: Double, y: Double, holdingShift: Boolean, holdingCtrl: Boolean)
    fun mouseUp( x: Double, y: Double, holdingShift: Boolean, holdingCtrl: Boolean)
    fun mouseDrag(x: Double, y: Double, holdingShift: Boolean, holdingCtrl: Boolean)

    val isDrawn : Boolean
    fun draw(gc:GraphicsContext)
}

class HitboxPenner(
    val toolset: IHitboxToolset)
    :IHitboxPenner
{
    override val selectedBoxBind = Bindable<AafHitbox?>(null)
    override var selectedBox by selectedBoxBind

    override var animation by OnChangeDelegate<AafAnimation?>(null) {behavior = null}
    override var met: Int by OnChangeDelegate(0) {behavior = null}

    var x: Double = 0.0
    var y: Double = 0.0
    var holdingShift = false
    var holdingCtrl = false

    var behavior : IHitboxPennerBehavior? = null
        set(value) {
            field?.onEnd(this)
            field = value
            value?.onStart(this)
        }

    override fun mouseDown(x: Double, y: Double, holdingShift: Boolean, holdingCtrl: Boolean) {
        this.x = x
        this.y = y
        this.holdingShift = holdingShift
        this.holdingCtrl = holdingCtrl
        val tool = toolset.tool
        when {
            holdingCtrl-> {}
            tool == HitboxTool.Rectangle -> behavior = BuildingRectangleBehavior()
        }
    }

    override fun mouseUp(x: Double, y: Double, holdingShift: Boolean, holdingCtrl: Boolean) {
        this.x = x
        this.y = y
        this.holdingShift = holdingShift
        this.holdingCtrl = holdingCtrl
        behavior?.mouseUp(this)

    }

    override fun mouseDrag(x: Double, y: Double, holdingShift: Boolean, holdingCtrl: Boolean) {
        this.x = x
        this.y = y
        this.holdingShift = holdingShift
        this.holdingCtrl = holdingCtrl
        behavior?.mouseDrag(this)
    }

    override val isDrawn: Boolean get() = behavior is IDrawnHitboxPennerBehavior
    override fun draw(gc: GraphicsContext) {
        (behavior as? IDrawnHitboxPennerBehavior)?.draw(this, gc)
    }
}

interface IHitboxPennerBehavior {
    fun onEnd(penner: HitboxPenner) {}
    fun onStart(penner: HitboxPenner){}
    fun mouseDown(penner: HitboxPenner) {}
    fun mouseUp( penner: HitboxPenner) {penner.behavior = null}
    fun mouseDrag(penner: HitboxPenner) {}
}

interface IDrawnHitboxPennerBehavior : IHitboxPennerBehavior {
    fun draw(penner: HitboxPenner, gc: GraphicsContext) {}
}

class BuildingRectangleBehavior : IDrawnHitboxPennerBehavior {
    var startX = 0.0
    var startY = 0.0

    override fun onStart(penner: HitboxPenner) {
        startX = penner.x
        startY = penner.y
    }

    override fun onEnd(penner: HitboxPenner) {
        val frame = penner.animation?.getFrame(penner.met) ?: return
        val uid = (frame.hboxes.asSequence().map { it.typeId }.max()?:0) + 1
        frame.hboxes.add(AafHitbox(uid.s, CollisionRigidRect(RectD.FromEndpoints(startX, startY, penner.x, penner.y))))
    }

    override fun draw(penner: HitboxPenner, gc: GraphicsContext) {
        gc.lineWidth = 1.5
        gc.stroke = Color.YELLOWGREEN
        val rect = RectD.FromEndpoints(startX, startY, penner.x, penner.y)
        gc.fillRect(rect.x1, rect.y1, rect.w, rect.h)

        gc.fill = Color(0.5, 0.7, 0.7, 0.5)
        gc.fillRect(rect.x1, rect.y1, rect.w, rect.h)
    }
}
