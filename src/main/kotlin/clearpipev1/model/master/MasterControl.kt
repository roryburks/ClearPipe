package clearpipev1.model.master

import clearpipev1.model.Dialog
import clearpipev1.model.IDialog
import clearpipev1.model.animation.MAafProject
import clearpipev1.model.master.settings.ISettingsManager
import clearpipev1.model.master.settings.JPreferences
import clearpipev1.model.master.settings.SettingsManager
import rb.owl.other.MSelectableList
import rb.owl.other.SelectableList
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