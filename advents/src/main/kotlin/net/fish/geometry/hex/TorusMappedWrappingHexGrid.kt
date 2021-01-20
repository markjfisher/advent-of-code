package net.fish.geometry.hex

import net.fish.geometry.Point3D
import net.fish.maths.normalFromPoints
import org.joml.Matrix3f
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class TorusMappedWrappingHexGrid(
    val hexGrid: WrappingHexGrid,
    val r1: Double = 10.0, // The radius of the minor circle, thinking of a doughnut, this is the smaller of the 2 circles
    val r2: Double = 50.0  // The radius of the major circle, the one which sweeps around dictating centre of minor circle
) {
    // return the coordinates of the hex corners on the torus described by the layout
    fun toroidCoordinates(hex: Hex): List<Point3D> {
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
            Point3D(p.y, p.z, -p.x)
        }
    }

    fun centres(): List<Point3D> {
        return hexGrid.hexes().map { hex -> toroidCoordinates(hex).last() }
    }

    fun hexAxes(): List<HexAxis> {
        return hexGrid.hexes().map { hex ->
            val cornersOnTorus = toroidCoordinates(hex)
            val centre = cornersOnTorus[6]
            val (xP, yP) = when(hexGrid.layout.orientation) {
                Orientation.ORIENTATION.POINTY -> {
                    val midpoint01 = (cornersOnTorus[0] + cornersOnTorus[1]) * 0.5
                    val midpoint34 = (cornersOnTorus[3] + cornersOnTorus[4]) * 0.5
                    val xDir = midpoint01 - midpoint34
                    val yDir = cornersOnTorus[2] - cornersOnTorus[5]
                    Pair(xDir, yDir)
                }
                Orientation.ORIENTATION.FLAT -> {
                    val xDir = cornersOnTorus[0] - cornersOnTorus[3]
                    val midpoint45 = (cornersOnTorus[4] + cornersOnTorus[5]) * 0.5
                    val midpoint21 = (cornersOnTorus[2] + cornersOnTorus[1]) * 0.5
                    val yDir = midpoint21 - midpoint45
                    Pair(xDir, yDir)
                }
            }
            val unitX = Vector3f(xP.x.toFloat(), xP.y.toFloat(), xP.z.toFloat()).normalize()
            val unitY = Vector3f(yP.x.toFloat(), yP.y.toFloat(), yP.z.toFloat()).normalize()
            val unitZ = unitX.cross(unitY, Vector3f())
            HexAxis(
                location = Vector3f(centre.x.toFloat(), centre.y.toFloat(), centre.z.toFloat()),
                axes = Matrix3f(unitX, unitY, unitZ)
            )
        }
    }

    // Create OBJ compatible output for the given hex, this is the 4 face bender style hex, minimizing faces, but they get bent on turns
    fun hexObj(hex: Hex): List<String> {
        val lines = mutableListOf<String>()
        // To be "blender" output compatible, we want to do points in order:
        // from: 2, 3, 4, 5, 0, 1
        //   to: 1, 2, 3, 4, 5, 6
        //    i: 0, 1, 2, 3, 4, 5
        // The 4 faces are from points  6/1/3, 1/2/3, 3/4/5, 5/6/3
        // which for our coordinates is 1/2/4, 2/3/4, 4/5/0, 0/1/4

        val toroidalCoordinates = toroidCoordinates(hex)

        // vetices
        for(i in (0..5)) {
            val corner = (i + 2) % 6 // convert from 0, 1, 2, .... to 2, 3, 4, ... as above.
            val c = toroidalCoordinates[corner]
            lines += String.format("v %7f %7f %7f", c.x, c.y, c.z)
        }

        // textures (unused)
        for (i in (0..5)) {
            lines += "vt 0.000000 0.000000"
        }

        // normals (4)
        for (t in listOf(Triple(1,2,4), Triple(2,3,4), Triple(4,5,0), Triple(0,1,4))) {
            val p1 = toroidalCoordinates[t.first]
            val p2 = toroidalCoordinates[t.second]
            val p3 = toroidalCoordinates[t.third]
            val normal = normalFromPoints(p1.toVector3f(), p2.toVector3f(), p3.toVector3f())
            lines += String.format("vn %5f %5f %5f", normal.x, normal.y, normal.z)
        }

        lines += "f 6/1/1 1/2/1 3/3/1"
        lines += "f 1/2/2 2/4/2 3/3/2"
        lines += "f 3/3/3 4/5/3 5/6/3"
        lines += "f 5/6/4 6/1/4 3/3/4"

        return lines.toList()
    }

    // Create OBJ compatible output for the given hex using 6 faces, with texture coordinates
    // mapped onto a torus of the given dimensions
    // The face triangles all share the centre as a common point, and fill the hexagon in a natural manner.
    fun toroidalHexObj(hex: Hex): List<String> {
        val lines = mutableListOf<String>()
        // Generate faces as follows to be more rounded than the 5 version by blender which doesn't bend well
        // 0/1/6, 1/2/6, 2/3/6, 3/4/6, 4/5/6, 5/0/6
        // This uses my origin which is SE for pointy, E for flat

        val toroidalCoordinates = toroidCoordinates(hex)

        // vetices (last one is the centre)
        for(i in (0..6)) {
            val c = toroidalCoordinates[i]
            lines += String.format("v %7f %7f %7f", c.x, c.y, c.z)
        }

        // textures
        when (hexGrid.layout.orientation) {
            Orientation.ORIENTATION.POINTY -> {
                lines += "vt 0.8660 0.7500" // point 0
                lines += "vt 0.8660 0.2500" // point 1
                lines += "vt 0.4330 0.0000" // point 2
                lines += "vt 0.0000 0.2500" // point 3
                lines += "vt 0.0000 0.7500" // point 4
                lines += "vt 0.4330 1.0000" // point 5
                lines += "vt 0.4330 0.5000" // point 6
            }
            Orientation.ORIENTATION.FLAT -> {
                // The y coordinate is shifted down by (1-sqrt(3)/2) as the 0,0 isn't top left it seems.
                lines += "vt 1.0000 0.5670" // point 0
                lines += "vt 0.7500 0.1340" // point 1
                lines += "vt 0.2500 0.1340" // point 2
                lines += "vt 0.0000 0.5670" // point 3
                lines += "vt 0.2500 1.0000" // point 4
                lines += "vt 0.7500 1.0000" // point 5
                lines += "vt 0.5000 0.5670" // point 6
            }
        }

        // normals (4)
        for (i in (0..5)) {
            val p1 = toroidalCoordinates[i]
            val p2 = toroidalCoordinates[(i + 1) % 6]
            val p3 = toroidalCoordinates[6]
            val normal = normalFromPoints(p1.toVector3f(), p2.toVector3f(), p3.toVector3f())
            lines += String.format("vn %5f %5f %5f", normal.x, normal.y, normal.z)
        }

        // faces are +1 compared to point number. make texture and point same
        lines += "f 1/1/1 2/2/1 7/7/1"
        lines += "f 2/2/2 3/3/2 7/7/2"
        lines += "f 3/3/3 4/4/3 7/7/3"
        lines += "f 4/4/4 5/5/4 7/7/4"
        lines += "f 5/5/5 6/6/5 7/7/5"
        lines += "f 6/6/6 1/1/6 7/7/6"

        return lines.toList()
    }

}