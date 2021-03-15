package clearpipe.model.master

import clearpipe.model.Dialog
import clearpipe.model.IDialog
import clearpipe.model.animation.MAafProject
import clearpipe.model.master.settings.ISettingsManager
import clearpipe.model.master.settings.JPreferences
import clearpipe.model.master.settings.SettingsManager
import old.rb.owl.other.MSelectableList
import old.rb.owl.other.SelectableList
import tornadofx.Controller

interface IMasterControl {
    val settings: ISettingsManager
    val dialog: IDialog
    val obs : ICentralObservatory
    val projectSet: MSelectableList<MAafProject>
    val dragManager: IDragManager

}

class MasterControl() : IMasterControl, Controller() {
    override val settings = SettingsManager(JPreferences(MasterControl::class.java))
    override val dialog = Dialog(settings)
    override val projectSet: MSelectableList<MAafProject> = SelectableList() // Needs to be before Obs
    override val obs = CentralObservatory(this)
    override val dragManager: IDragManager = DragManager()
}