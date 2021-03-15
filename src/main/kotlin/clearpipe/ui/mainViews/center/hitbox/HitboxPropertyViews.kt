package clearpipe.ui.mainViews.center.hitbox

import clearpipe.model.animation.AafHitboxK
import javafx.scene.Parent
import rb.owl.Observable
import old.rb.owl.bindable.Bindable
import old.rb.owl.bindable.addObserver
import old.rb.vectrix.intersect.CollisionCircle
import old.rb.vectrix.intersect.CollisionRigidRect
import old.rb.vectrix.shapes.Circle
import old.rb.vectrix.shapes.CircleD
import old.rb.vectrix.shapes.Rect
import old.rb.vectrix.shapes.RectD
import tornadofx.*

object HitboxPropertyViewFactory
{
    fun buildView(hitbox : AafHitboxK?, hitboxObservable: Observable<HitboxTrigger>) : View? {
        val mod = when( val col = hitbox?.col){
            is CollisionRigidRect -> RectView(col.rect)
            is CollisionCircle -> CircleView(col.circle)
            else -> return null
        }

        return BaseView(hitbox, hitboxObservable, mod).also { mod.base = it }
    }
}

class HitboxPropertyView(private val hitboxObservable: Observable<HitboxTrigger>) : View() {
    val hitboxBind = Bindable<AafHitboxK?>(null)

    override val root = hbox {  }

    init {
        hitboxBind.addObserver { new, old ->
            root.clear()
            HitboxPropertyViewFactory.buildView(new, hitboxObservable)?.also { root.add(it)}
        }
    }
}

private class BaseView(
    val hitbox: AafHitboxK,
    val hitboxObservable: Observable<HitboxTrigger>,
    val modView: ModView)
    : View()
{
    val tfTypeId = textfield(hitbox.typeId.toString())

    override val root: Parent = vbox {
        spacing = 1.0
        hbox {
            spacing = 3.0
            label("TypeId: ")
            add(tfTypeId)
        }
        add(modView)
        hbox {
            button("Apply") {
                action {
                    hitbox.typeId = tfTypeId.text.toShort()
                    modView.onApply()
                    hitboxObservable.trigger { it()}
                }
            }
            button("Delete") {
                action {
                    hitbox.context.removeHBox(hitbox)
                    hitboxObservable.trigger { it()}
                }
            }
        }
    }
}

private abstract class ModView : View() {
    lateinit var base: BaseView
    abstract fun onApply()
}

private class CircleView(circle: Circle)
    : ModView()
{
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
    }

    override fun onApply() {
        base.hitbox.col = CollisionCircle(CircleD.Make(
            xtf.text.toDouble(),
            ytf.text.toDouble(),
            rtf.text.toDouble()))
    }
}

private class RectView(rect: Rect)
    : ModView()
{
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
    }

    override fun onApply() {
        base.hitbox.col = CollisionRigidRect(RectD(
            xtf.text.toDouble(),
            ytf.text.toDouble(),
            wtf.text.toDouble(),
            htf.text.toDouble()))
    }
}