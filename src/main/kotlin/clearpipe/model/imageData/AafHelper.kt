package clearpipe.model.imageData

import javafx.scene.canvas.GraphicsContext

fun AafAnimation.draw(gc: GraphicsContext, frame: Int)
{
    val draws = this.getDraws(frame)
    val ox = this.frames

    draws.forEach {
//        gc.drawImage()
    }
}