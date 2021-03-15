package clearpipe.ui.mainViews.center.hitbox

import clearpipe.model.imageData.AafAnimation
import clearpipe.model.imageData.AafHitbox
import clearpipe.ui.mainViews.center.hitbox.behavior.BuildingCircleBehavior
import clearpipe.ui.mainViews.center.hitbox.behavior.BuildingRectangleBehavior
import javafx.scene.canvas.GraphicsContext
import old.rb.extendo.delegates.OnChangeDelegate
import rb.owl.Observable
import old.rb.owl.bindable.Bindable
import old.rb.vectrix.intersect.CollisionPoint

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
    val toolset: IHitboxToolset,
    val hitboxChangeObservable : Observable<HitboxTrigger>
)
    :IHitboxPenner
{
    fun trigger() = hitboxChangeObservable.trigger { it() }

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
            holdingCtrl->  selectedBox = getSelected() ?: selectedBox
            tool == HitboxTool.Rectangle -> behavior = BuildingRectangleBehavior()
            tool == HitboxTool.Circle -> behavior = BuildingCircleBehavior()
        }
    }

    fun getSelected() : AafHitbox? {
        val point = CollisionPoint(x,y)
        return animation?.getFrame(met)?.hboxes?.firstOrNull{point.intersects(it.col)}
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

