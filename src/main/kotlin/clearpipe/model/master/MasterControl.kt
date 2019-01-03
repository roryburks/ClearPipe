package clearpipe.model.master

import clearpipe.model.Dialog
import clearpipe.model.IDialog
import clearpipe.model.imageData.AafProject
import clearpipe.model.imageData.IAafProject
import clearpipe.model.imageData.MAafProject
import rb.owl.other.ISelectableList
import rb.owl.other.MSelectableList
import rb.owl.other.SelectableList
import tornadofx.Controller

interface IMasterControl {
    val dialog: IDialog
    val obs : ICentralObservatory
    val projectSet: MSelectableList<MAafProject>
    val dragManager: IDragManager

}

class MasterControl() : IMasterControl, Controller() {
    override val dialog = Dialog()
    override val projectSet: MSelectableList<MAafProject> = SelectableList() // Needs to be before Obs
    override val obs = CentralObservatory(this)
    override val dragManager: IDragManager = DragManager()
}