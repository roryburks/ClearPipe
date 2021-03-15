package clearpipe.ui.mainViews.center.hitbox

import old.rb.owl.bindable.Bindable

enum class HitboxTool(val btn: String, val desc: String) {
    Rectangle("R", "Rectangle"),
    Point(".","Point"),
    RayRect("Y","Ray Rect"),
    Polygon("P", "Polygon"),
    Circle("O", "Circle"),
    LineSegment("|","Line Segment")
    ;
}

interface IHitboxToolset {
    val toolBind : Bindable<HitboxTool>
    var tool : HitboxTool
}

class HitboxToolset : IHitboxToolset {
    override val toolBind = Bindable(HitboxTool.Rectangle)
    override var tool: HitboxTool by toolBind
}