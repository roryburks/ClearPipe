package clearpipev1.canvasFxDraws

import rb.vectrix.intersect.CollisionCircle
import rb.vectrix.intersect.CollisionObject
import rb.vectrix.intersect.CollisionRigidRect
import rb.vectrix.shapes.CircleD
import rb.vectrix.shapes.RectD

fun CollisionObject.shift(x: Double, y: Double) : CollisionObject{
    return when(this) {
        is CollisionRigidRect -> CollisionRigidRect(RectD(rect.x1+ x, rect.y1 + y, rect.w, rect.h))
        is CollisionCircle -> CollisionCircle(CircleD.Make(circle.x + x, circle.y + y, circle.r))
        else -> TODO()
    }
}