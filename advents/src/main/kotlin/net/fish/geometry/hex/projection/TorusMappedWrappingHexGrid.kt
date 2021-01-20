package net.fish.geometry.hex.projection

import net.fish.geometry.Point3D
import net.fish.geometry.hex.Hex
import net.fish.geometry.hex.WrappingHexGrid
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class TorusMappedWrappingHexGrid(
    override val hexGrid: WrappingHexGrid,
    val r1: Double, // The radius of the minor circle, thinking of a doughnut, this is the smaller of the 2 circles
    val r2: Double  // The radius of the major circle, the one which sweeps around dictating centre of minor circle
): ProjectionMapper(hexGrid) {

    override fun coordinates(hex: Hex): List<Vector3f> {
        // See https://gamedev.stackexchange.com/questions/16845/how-do-i-generate-a-torus-mesh
        // The coordinate system here is LH based on the solution from stack overflow
        fun w(theta: Double): Point3D = Point3D(cos(theta), sin(theta), 0.0)
        fun q(theta: Double, phi: Double): Point3D = w(theta) * r2 + w(theta) * cos(phi) * r1 + Point3D(0.0, 0.0, r1 * sin(phi))

        val centre = hexGrid.layout.hexToPixel(hex)
        return (hexGrid.layout.polygonCorners(hex) + centre).map {
            val theta = 2.0 * PI * it.x / hexGrid.width()
            val phi = 2.0 * PI * (1.0 - it.y / hexGrid.height())

            val p = q(theta, phi)
            // Convert to RH coordinates
            Vector3f(p.y.toFloat(), p.z.toFloat(), -p.x.toFloat())
        }
    }

}