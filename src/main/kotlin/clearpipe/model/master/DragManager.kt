package clearpipe.model.master

import javafx.scene.input.DataFormat

interface IDragManager {
    val drag: DragSource?
    fun startDragging(format: DataFormat, content: Any?)
    fun startDragging(dragSource: DragSource)
}

class DragSource() {
    private var set = mutableMapOf<DataFormat,Any?>()
    fun add( format: DataFormat, content: Any?) {
        set[format] = content
    }
    fun get( format: DataFormat) : Any? = set[format]
    fun supports(format: DataFormat) = set.containsKey(format)
}

class DragManager : IDragManager {
    override var drag: DragSource? = null

    override fun startDragging(format: DataFormat, content: Any?) {
        drag = DragSource().also { it.add(format, content) }
    }

    override fun startDragging(dragSource: DragSource) {
        drag = dragSource
    }
}