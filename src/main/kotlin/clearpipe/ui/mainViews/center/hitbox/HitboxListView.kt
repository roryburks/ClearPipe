package clearpipe.ui.mainViews.center.hitbox

import clearpipe.model.imageData.AafFrame
import clearpipe.model.imageData.AafHitbox
import javafx.collections.FXCollections
import javafx.scene.control.ListCell
import javafx.util.Callback
import old.rb.extendo.delegates.OnChangeDelegate
import old.rb.vectrix.intersect.CollisionCircle
import old.rb.vectrix.intersect.CollisionRigidRect
import tornadofx.Controller
import tornadofx.View
import tornadofx.listview
import tornadofx.vbox

class HitboxListView : View()
{
    val controller = HitboxListController()
    val listView = listview(controller.values)
    var frame by OnChangeDelegate<AafFrame?>(null) {rebuild()}

    override val root = vbox {
        add(listView)
        listView.cellFactory = Callback { HitboxListCell() }
    }

    fun rebuild() {
        controller.values.clear()
        frame?.run { controller.values.addAll(this.hboxes) }
    }
}

class HitboxListCell : ListCell<AafHitbox>() {
    override fun updateItem(item: AafHitbox?, empty: Boolean) {
        text = when(val col = item?.col) {
            null -> ""
            is CollisionRigidRect -> "Rect Hitbox: ${col.rect}"
            is CollisionCircle -> "Circular Hitbox: ${col.circle}"
            else -> "Unknown Hitbox"
        }
        super.updateItem(item, empty)
    }

}

class HitboxListController : Controller()
{
    val values = FXCollections.observableArrayList<AafHitbox>()
}