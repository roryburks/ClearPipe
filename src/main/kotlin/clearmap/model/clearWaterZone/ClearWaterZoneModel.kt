package clearmap.model.clearWaterZone

import old.rb.owl.bindableMList.BindableMListSet
import old.rb.vectrix.shapes.RectI
import java.awt.Image

class ClearWaterZoneModel {
    val celSets = BindableMListSet<CelSetModel>()
}

class CelSetModel(val img : Image)
{
    val cels = BindableMListSet<RectI>()
}
