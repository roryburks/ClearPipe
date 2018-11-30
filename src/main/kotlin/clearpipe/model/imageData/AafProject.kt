package clearpipe.model.imageData

import javafx.scene.image.Image
import org.w3c.dom.css.Rect
import rb.owl.bindable.MBindable
import rb.owl.bindableMList.BindableMList

interface IAafProject {
    val animations: List<AafAnimation>

    val celSetsBind: BindableMList<CelSet>
    val celSets: List<CelSet>

    fun import( animations: List<AafAnimation>, celset: CelSet)
}

class AafProject {
}

class AafAnimation(
    var name: String,
    val frames: List<AafFrame>
)
class AafFrame(
    val chunks: List<AafChunk>
)
class AafChunk(
    val celId: Int,
    val offsetX: Short,
    val offsetY: Short,
    val drawDepth: Int)

class CelSet(val image: Image, val cels: List<Rect>)