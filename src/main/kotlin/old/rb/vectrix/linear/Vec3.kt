package old.rb.vectrix.linear

import old.rb.vectrix.mathUtil.d

abstract class Vec3{
    abstract val x: Double
    abstract val y: Double
    abstract val z: Double

    // TODO:
    //operator fun plus( rhs: Vec3) : Vec3
    //operator fun minus( rhs: Vec3) : Vec3
    //operator fun times( rhs: Double)
    // etc
}

data class Vec3d(
    override val x: Double,
    override val y: Double,
    override val z: Double)
    : Vec3()

data class Vec3f(
    val xf: Float,
    val yf: Float,
    val zf: Float)
    : Vec3()
{
    override val x: Double get() = xf.d
    override val y: Double get() = yf.d
    override val z: Double get() = zf.d
}