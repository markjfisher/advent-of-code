package net.fish.geometry.hex

import net.fish.geometry.hex.Orientation.ORIENTATION.FLAT
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
import org.joml.Matrix3f
import org.joml.Vector3d
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
        fun w(theta: Double): Point3D = Point3D(cos(theta), sin(theta), 0.0)
        fun q(theta: Double, phi: Double): Point3D = w(theta) * r2 + w(theta) * cos(phi) * r1 + Point3D(0.0, 0.0, r1 * sin(phi))

        val centre = layout.hexToPixel(hex)
        return (layout.polygonCorners(hex) + centre).map {
            val theta = 2.0 * PI * it.x / width()
            val phi = 2.0 * PI * (1.0 - it.y / height())

            q(theta, phi)
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
                    val xDir = midpoint34 - midpoint01
                    val yDir = cornersOnTorus[5] - cornersOnTorus[2]
                    Pair(xDir, yDir)
                }
                FLAT -> {
                    val xDir = cornersOnTorus[3] - cornersOnTorus[0]
                    val midpoint45 = (cornersOnTorus[4] + cornersOnTorus[5]) * 0.5
                    val midpoint21 = (cornersOnTorus[2] + cornersOnTorus[1]) * 0.5
                    val yDir = midpoint45 - midpoint21
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
}