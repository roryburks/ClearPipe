package clearmap.model.clearWaterZone

import rb.extendo.delegates.OnChangeDelegate
import rb.owl.bindableMList.BindableMListSet
import rb.vectrix.shapes.RectI
import java.awt.Image

class ClearWaterZoneModel {
    val celSets = BindableMListSet<CelSetModel>()
}

class CelSetModel(val img : Image)
{
    val cels = BindableMListSet<RectI>()
}
