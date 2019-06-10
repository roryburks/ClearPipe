package clearmap.model.clearWaterZone

import rb.extendo.delegates.OnChangeDelegate
import rb.owl.bindableMList.BindableMListSet


class CwzTileSetModel {
    val tiles = BindableMListSet<CwzTileModel>()

    fun tileUpdated( tile: CwzTileModel) {}
}

class CwzTileModel(
    val set: CwzTileSetModel,
    val cel: CelSetModel,
    ox: Double,
    oy: Double,
    rot: Double,
    sx: Double,
    sy: Double)
{
    var ox by OnChangeDelegate(ox) { set.tileUpdated(this) }
    var oy by OnChangeDelegate(oy) { set.tileUpdated(this) }
    var rot by OnChangeDelegate(rot) { set.tileUpdated(this) }
    var sx by OnChangeDelegate(sx) { set.tileUpdated(this) }
    var sy by OnChangeDelegate(sy) { set.tileUpdated(this) }
}