package clearpipe.canvasFxDraws

import old.rb.vectrix.intersect.CollisionCircle
import old.rb.vectrix.intersect.CollisionObject
import old.rb.vectrix.intersect.CollisionRigidRect
import old.rb.vectrix.shapes.CircleD
import old.rb.vectrix.shapes.RectD

fun CollisionObject.shift(x: Double, y: Double) : CollisionObject{
    return when(this) {
        is CollisionRigidRect -> CollisionRigidRect(RectD(rect.x1+ x, rect.y1 + y, rect.w, rect.h))
        is CollisionCircle -> CollisionCircle(CircleD.Make(circle.x + x, circle.y + y, circle.r))
        else -> TODO()
    }
}