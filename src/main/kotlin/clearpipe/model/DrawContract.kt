package clearpipe.model

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import rb.vectrix.shapes.Rect
import rb.vectrix.shapes.RectI

data class DrawContract(
    val image: Image,
    val area: Rect,
    val offsetX: Double,
    val offsetY: Double)

fun DrawContract.draw(gc: GraphicsContext)
        = gc.drawImage(image, area.x1, area.y1, area.w, area.h, offsetX, offsetY, area.w,  area.h)