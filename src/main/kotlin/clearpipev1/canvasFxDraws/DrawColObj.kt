package clearpipev1.canvasFxDraws

import javafx.scene.canvas.GraphicsContext
import rb.vectrix.intersect.*

fun CollisionObject.draw(gc: GraphicsContext){
    when(this) {
        is CollisionPoint -> gc.strokeOval(x-0.5,y-0.5, 1.0, 1.0)
        is CollisionRigidRect -> {
            gc.fillRect(rect.x1, rect.y1, rect.w, rect.h)
            gc.strokeRect(rect.x1, rect.y1, rect.w, rect.h)
        }
        is CollisionRayRect -> {
            val xs = this.rayRect.points.map { it.x }.toDoubleArray()
            val ys = this.rayRect.points.map { it.y }.toDoubleArray()
            gc.fillPolygon(xs, ys, xs.size)
            gc.strokePolygon(xs, ys, xs.size)
        }
        is CollisionPolygon -> {
            val xs = this.polygon.vertices.map { it.x }.toDoubleArray()
            val ys = this.polygon.vertices.map { it.y }.toDoubleArray()
            gc.fillPolygon(xs, ys, xs.size)
            gc.strokePolygon(xs, ys, xs.size)
        }
        is CollisionParabola -> {}
        is CollisionCircle -> {
            gc.fillOval(circle.x - circle.r, circle.y - circle.r, circle.r*2, circle.r*2)
            gc.strokeOval(circle.x - circle.r, circle.y - circle.r, circle.r*2, circle.r*2)
        }
        is CollisionLineSegment -> lineSegment.run { gc.strokeLine(x1, y1, x2, y2) }
    }
}