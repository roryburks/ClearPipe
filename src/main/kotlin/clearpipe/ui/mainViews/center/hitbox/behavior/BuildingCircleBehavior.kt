package clearpipe.ui.mainViews.center.hitbox.behavior

import clearpipe.model.imageData.AafHitbox
import clearpipe.ui.mainViews.center.hitbox.HitboxPenner
import clearpipe.ui.mainViews.center.hitbox.IDrawnHitboxPennerBehavior
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import rb.vectrix.intersect.CollisionCircle
import rb.vectrix.intersect.CollisionRigidRect
import rb.vectrix.mathUtil.MathUtil
import rb.vectrix.mathUtil.s
import rb.vectrix.shapes.CircleD
import rb.vectrix.shapes.RectD

class BuildingCircleBehavior : IDrawnHitboxPennerBehavior {
    var startX = 0.0
    var startY = 0.0

    override fun onStart(penner: HitboxPenner) {
        startX = penner.x
        startY = penner.y
    }

    override fun onEnd(penner: HitboxPenner) {
        val frame = penner.animation?.getFrame(penner.met) ?: return
        val dist = MathUtil.distance(startX, startY, penner.x, penner.y)
        val uid = (frame.hboxes.asSequence().map { it.typeId }.max()?:0) + 1

        if( dist >= 1) {
            val hitbox = AafHitbox(uid.s, CollisionCircle(CircleD.Make(startX, startY, dist)))
            frame.addHBox(hitbox)
            penner.selectedBox = hitbox
            penner.trigger()
        }
    }

    override fun draw(penner: HitboxPenner, gc: GraphicsContext) {
        val dist = MathUtil.distance(startX, startY, penner.x, penner.y)

        gc.lineWidth = 1.5
        gc.stroke = Color.YELLOWGREEN
        gc.strokeOval(startX - dist, startY - dist, dist*2, dist*2)

        gc.fill = Color(0.5, 0.7, 0.7, 0.5)
        gc.fillOval(startX - dist, startY - dist, dist*2, dist*2)
    }
}