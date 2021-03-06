package clearpipev1.ui.mainViews.center.hitbox.behavior

import clearpipev1.model.animation.AafHitboxK
import clearpipev1.ui.mainViews.center.hitbox.HitboxPenner
import clearpipev1.ui.mainViews.center.hitbox.IDrawnHitboxPennerBehavior
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import rb.vectrix.intersect.CollisionRigidRect
import rb.vectrix.mathUtil.s
import rb.vectrix.shapes.RectD

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

        if( startX != penner.x && startY != penner.y) {
            val hitbox = AafHitboxK(uid.s, CollisionRigidRect(RectD.FromEndpoints(startX, startY, penner.x, penner.y)))
            frame.addHBox(hitbox)
            penner.selectedBox = hitbox
            penner.trigger()
        }
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