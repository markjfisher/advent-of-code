package net.fish.geometry.hex

import net.fish.geometry.hex.Orientation.ORIENTATION.FLAT
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import org.joml.Matrix3f
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/*
 This is a rectangular grid of Hex objects, whose ends wrap around.
 Thus making a Toroid in topology.
 This class required a lot of sweat, new ink for hex graphs, 2 pencils, 1 eraser, and quite a bit of wine.
 */
data class WrappingHexGrid(
    val m: Int,
    val n: Int,
    val layout: Layout,
    val r1: Double = 10.0, // The radius of the minor circle, thinking of a doughnut, this is the smaller of the 2 circles
    val r2: Double = 50.0  // The radius of the major circle, the one which sweeps around dictating centre of minor circle
) : HexConstrainer {
    init {
        require(if (layout.orientation == POINTY) n % 2 == 0 else true) {
            "Invalid dimensions for pointy orientation, n must be even. Given: $n"
        }

        require(if (layout.orientation == FLAT) m % 2 == 0 else true) {
            "Invalid dimensions for flat orientation, m must be even. Given: $m"
        }
    }

    // This is in terms of m/n, and needs to be multiplied by the layout's hex size to get an actual width/height
    fun width(): Double = when (layout.orientation) {
        POINTY -> m * layout.orientation.o.f0 // f0 = sqrt(3)
        FLAT -> 1.5 * m
    }

    fun height(): Double = when (layout.orientation) {
        POINTY -> 1.5 * n
        FLAT -> n * layout.orientation.o.f3 // f3 = sqrt(3)
    }

    // Magic constants for constraining to pointy grid
    private val minPointyR = 1 - n
    private val maxPointyR = 0
    private val maxPointyQminusS = 2 * m - 1
    private val minPointyQminusS = 0

    // Magic constants for constraining to flat grid
    private val minFlatRminusS = 1 - 2 * n
    private val maxFlatRminusS = 0
    private val minFlatQ = 0
    private val maxFlatQ = m - 1

    fun hex(q: Int, r: Int, s: Int): Hex = constrain(Hex(q, r, s, this))

    override tailrec fun constrain(hex: Hex): Hex {
        val adjust = when (layout.orientation) {
            POINTY -> adjust(hex.r, minPointyR, maxPointyR, Hex(-n / 2, n, -n / 2), hex.q, hex.s, minPointyQminusS, maxPointyQminusS, Hex(m, 0, -m))
            FLAT -> adjust(hex.q, minFlatQ, maxFlatQ, Hex(m, -m / 2, -m / 2), hex.r, hex.s, minFlatRminusS, maxFlatRminusS, Hex(0, n, -n))
        }
        return if (adjust == Hex(0, 0, 0)) Hex(hex.q, hex.r, hex.s, this) else constrain(Hex(hex.q + adjust.q, hex.r + adjust.r, hex.s + adjust.s))
    }

    private fun adjust(hA: Int, minA: Int, maxA: Int, offsetA: Hex, hB: Int, hC: Int, minBC: Int, maxBC: Int, offsetBC: Hex): Hex {
        var adjust = Hex(0, 0, 0)
        // hA is the hex component that is purely horizontal or vertical (depending on grid orientation)
        // e.g. for pointy, this is hex.r, for flat this is hex.q
        if (hA < minA) adjust = Hex(adjust.q + offsetA.q, adjust.r + offsetA.r, adjust.s + offsetA.s)
        if (hA > maxA) adjust = Hex(adjust.q - offsetA.q, adjust.r - offsetA.r, adjust.s - offsetA.s)
        // hB/hC are the non- horizontal/vertical components which when subtracted give perpendicular vector to hA
        if (hB - hC < minBC) adjust = Hex(adjust.q + offsetBC.q, adjust.r + offsetBC.r, adjust.s + offsetBC.s)
        if (hB - hC > maxBC) adjust = Hex(adjust.q - offsetBC.q, adjust.r - offsetBC.r, adjust.s - offsetBC.s)
        return adjust
    }

    // return the coordinates of the hex corners on the torus described by the layout
    fun toroidCoordinates(hex: Hex): List<Point3D> {
        // See https://gamedev.stackexchange.com/questions/16845/how-do-i-generate-a-torus-mesh
        // The coordinate system here is LH based on the solution from stack overflow
        fun w(theta: Double): Point3D = Point3D(cos(theta), sin(theta), 0.0)
        fun q(theta: Double, phi: Double): Point3D = w(theta) * r2 + w(theta) * cos(phi) * r1 + Point3D(0.0, 0.0, r1 * sin(phi))

        val centre = layout.hexToPixel(hex)
        return (layout.polygonCorners(hex) + centre).map {
            val theta = 2.0 * PI * it.x / width()
            val phi = 2.0 * PI * (1.0 - it.y / height())

            val p = q(theta, phi)
            // Convert to RH coordinates
            Point3D(p.y, p.z, -p.x)
        }
    }

    fun hexes(): Iterable<Hex> {
        return sequence {
            when (layout.orientation) {
                POINTY -> for (r in 0 until n) {
                    for (q in 0 until m) {
                        val qc = q + r / 2 + r % 2
                        val rc = -r
                        val sc = 0 - qc - rc
                        yield(Hex(qc, rc, sc, this@WrappingHexGrid))
                    }
                }
                FLAT -> for (s in 0 until n) {
                    for (q in 0 until m) {
                        val qc = q
                        val sc = s - q / 2
                        val rc = 0 - qc - sc
                        yield(Hex(qc, rc, sc, this@WrappingHexGrid))
                    }
                }
            }
        }.asIterable()
    }

    // This considers a single object for the entire torus, but isn't actually going to render this way
    // (unless we want it as a surface for the other points to overlay)
    fun mesh(): HexGridMesh {
        val allPointsMap = mutableMapOf<Int, Point3D>()
        val indices = mutableListOf<Int>()
        var pointIndex = 0
        hexes().forEach { hex ->
            // accumulate 7 unique points of the hex into a list, with their indexes in the global points map
            val hexPointIndices = mutableListOf<Int>()
            toroidCoordinates(hex).forEach { hexPoint ->
                val theIndex: Int
                val duplicatePoints = allPointsMap.filterValues { (it - hexPoint).length() < 0.001 }
                if (duplicatePoints.isEmpty()) {
                    // Create a new entry in global map, track the point and its index
                    theIndex = pointIndex++
                    allPointsMap[theIndex] = hexPoint
                } else {
                    // use the actual one previously stored in the map to remove any rounding errors
                    theIndex = duplicatePoints.entries.first().key
                }
                hexPointIndices += theIndex
            }
            // we now have 7 points and their indexes into the global points map, so add to the mesh the triangle combinations
            // These are strictly in the same order as we look at hex corners
            indices.addAll(listOf(hexPointIndices[0], hexPointIndices[1], hexPointIndices[6]))
            indices.addAll(listOf(hexPointIndices[1], hexPointIndices[2], hexPointIndices[6]))
            indices.addAll(listOf(hexPointIndices[2], hexPointIndices[3], hexPointIndices[6]))
            indices.addAll(listOf(hexPointIndices[3], hexPointIndices[4], hexPointIndices[6]))
            indices.addAll(listOf(hexPointIndices[4], hexPointIndices[5], hexPointIndices[6]))
            indices.addAll(listOf(hexPointIndices[5], hexPointIndices[0], hexPointIndices[6]))
        }
        val points = allPointsMap.toSortedMap().values.toList()

        return HexGridMesh(points, indices)
    }

    fun centres(): List<Point3D> {
        return hexes().map { hex -> toroidCoordinates(hex).last() }
    }

    fun hexAxes(): List<HexAxis> {
        return hexes().map { hex ->
            val cornersOnTorus = toroidCoordinates(hex)
            val centre = cornersOnTorus[6]
            val (xP, yP) = when(layout.orientation) {
                POINTY -> {
                    val midpoint01 = (cornersOnTorus[0] + cornersOnTorus[1]) * 0.5
                    val midpoint34 = (cornersOnTorus[3] + cornersOnTorus[4]) * 0.5
                    val xDir = midpoint01 - midpoint34
                    val yDir = cornersOnTorus[2] - cornersOnTorus[5]
                    Pair(xDir, yDir)
                }
                FLAT -> {
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

    // Create OBJ compatible output for the given hex
    fun hexObj2(hex: Hex): List<String> {
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
        when (layout.orientation) {
            POINTY -> {
                lines += "vt 0.8660 0.7500" // point 0
                lines += "vt 0.8660 0.2500" // point 1
                lines += "vt 0.4330 0.0000" // point 2
                lines += "vt 0.0000 0.2500" // point 3
                lines += "vt 0.0000 0.7500" // point 4
                lines += "vt 0.4330 1.0000" // point 5
                lines += "vt 0.4330 0.5000" // point 6
            }
            FLAT -> {
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

    companion object {
        fun normalFromPoints(p1: Vector3f, p2: Vector3f, p3: Vector3f): Vector3f {
            // Blender does normal = (p1-p2)x(p2-p3)
            return (p1.sub(p2, Vector3f()).cross(p2.sub(p3, Vector3f()))).normalize()
        }
    }

}