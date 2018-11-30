package clearpipe.ui.mainViews

import clearpipe.model.master.IMasterControl
import clearpipe.model.imageData.CelSet
import javafx.collections.FXCollections
import rb.owl.bindableMList.BindableMList
import tornadofx.*

class DisplayCelSpaceView(private val master: IMasterControl)
    : View()
{

    // region UI objects
    val listView = listview<CelSet>()
    val canvas = canvas()
    override val root = scrollpane {
        canvas
    }
    // endregion
}

class DisplayCelSpaceController(private val master: IMasterControl) : Controller() {
    val celListBind = BindableMList<CelSet>().also {  }
    val cellLists = FXCollections.observableArrayList<CelSet>()
}
