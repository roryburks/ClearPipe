package clearpipe.ui.mainViews.center.hitbox

import clearpipe.model.imageData.AafHitbox
import javafx.scene.Parent
import rb.owl.Observable
import rb.owl.bindable.Bindable
import rb.owl.bindable.addObserver
import rb.vectrix.intersect.CollisionCircle
import rb.vectrix.intersect.CollisionRigidRect
import rb.vectrix.shapes.Circle
import rb.vectrix.shapes.CircleD
import rb.vectrix.shapes.Rect
import rb.vectrix.shapes.RectD
import tornadofx.*

object HitboxPropertyViewFactory
{
    fun buildView(hitbox : AafHitbox?, hitboxObservable: Observable<HitboxTrigger>) : View? {
        return when( val col = hitbox?.col) {
            is CollisionRigidRect -> RectView(hitbox, hitboxObservable, col.rect)
            is CollisionCircle -> CircleView(hitbox, hitboxObservable, col.circle)
            else -> null
        }
    }
}

class HitboxPropertyView(val hitboxObservable: Observable<HitboxTrigger>) : View() {
    val hitboxBind = Bindable<AafHitbox?>(null)

    override val root = hbox {  }

    init {
        hitboxBind.addObserver { new, old ->
            root.clear()
            HitboxPropertyViewFactory.buildView(new, hitboxObservable)?.also { root.add(it)}
        }
    }
}

private class CircleView( val hitbox: AafHitbox, val hitboxObservable: Observable<HitboxTrigger>, circle: Circle) : View() {
    val xtf = textfield(circle.x.toString())
    val ytf = textfield(circle.y.toString())
    val rtf = textfield(circle.r.toString())
    override val root = vbox {
        spacing = 1.0
        hbox {
            spacing = 3.0
            label("x")
            add(xtf)
            label("y")
            add(ytf)
        }
        hbox {
            spacing = 3.0
            label("r")
            add(rtf)
        }
        hbox {
            button("Apply") {
                action {
                    hitbox.col = CollisionCircle(CircleD.Make(
                        xtf.text.toDouble(),
                        ytf.text.toDouble(),
                        rtf.text.toDouble()))
                    hitboxObservable.trigger { it()}
                }
            }
        }
    }
}

private class RectView(val hitbox: AafHitbox, val hitboxObservable: Observable<HitboxTrigger>, rect: Rect) : View() {
    val xtf = textfield(rect.x1.toString())
    val ytf = textfield(rect.y1.toString())
    val wtf = textfield(rect.w.toString())
    val htf = textfield(rect.h.toString())
    override val root = vbox {
        spacing = 1.0
        hbox {
            spacing = 3.0
            label("x")
            add(xtf)
            label("y")
            add(ytf)
        }
        hbox {
            spacing = 3.0
            label("w")
            add(wtf)
            label("h")
            add(htf)
        }
        hbox {
            button("Apply") {
                action {
                    hitbox.col = CollisionRigidRect(RectD(
                        xtf.text.toDouble(),
                        ytf.text.toDouble(),
                        wtf.text.toDouble(),
                        htf.text.toDouble()))
                    hitboxObservable.trigger { it()}
                }
            }
        }
    }
}