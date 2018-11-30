package clearpipe.model.master

import clearpipe.model.Dialog
import clearpipe.model.IDialog
import tornadofx.Controller

interface IMasterControl {
    val dialog: IDialog

}

class MasterControl() : IMasterControl, Controller() {
    override val dialog = Dialog()
}