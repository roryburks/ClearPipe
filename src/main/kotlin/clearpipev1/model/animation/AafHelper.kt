package clearpipev1.model.animation

import javafx.scene.canvas.GraphicsContext

fun AafAnimationK.draw(gc: GraphicsContext, frame: Int)
{
    val draws = this.getDraws(frame)
    val ox = this.frames

    draws.forEach {
//        gc.drawImage()
    }
}