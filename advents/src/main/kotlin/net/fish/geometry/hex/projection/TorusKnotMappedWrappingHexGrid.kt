package net.fish.geometry.hex.projection

import net.fish.geometry.hex.Hex
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.geometry.knots.Knots
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// This will create the coordinates for any hexagon on the Torus Knot.
// The hexGrid controls movement within the hexagons, but this class presents the real world coordinates of
// each hexagon mapped onto the given Torus Knot
data class TorusKnotMappedWrappingHexGrid(
    override val hexGrid: WrappingHexGrid,
    val p: Int,
    val q: Int,
    val a: Double = 1.0,
    val b: Double = 0.5,
    val r: Double = 0.2,
    val scale: Double = 1.0
): ProjectionMapper(hexGrid) {
    private val knotCoordinates = Knots.torusKnot(p, q, a, b, scale, hexGrid.m * 2)
    // private val knotCoordinates = Knots.wikiTrefoilCoordinates(hexGrid.m * 2)
    // private val knotCoordinates = Knots.decoratedKnot4d(hexGrid.m * 2)
    // private val knotCoordinates = Knots.decoratedKnot7a(hexGrid.m * 2)
    // private val knotCoordinates = Knots.decoratedKnot13c(hexGrid.m * 2)
    private val hexCentres = mutableMapOf<Hex, Vector3f>()

    init {
        calculateHexCentres()
    }

    override fun coordinates(hex: Hex): List<Vector3f> {
        // for a given hex, its corners are to be calculated relative to the hexes around it for simplicity
        // until we see how bad this is...
        val c0 = averageCentres(hex, hex.neighbour(5), hex.neighbour(0))
        val c1 = averageCentres(hex, hex.neighbour(0), hex.neighbour(1))
        val c2 = averageCentres(hex, hex.neighbour(1), hex.neighbour(2))
        val c3 = averageCentres(hex, hex.neighbour(2), hex.neighbour(3))
        val c4 = averageCentres(hex, hex.neighbour(3), hex.neighbour(4))
        val c5 = averageCentres(hex, hex.neighbour(4), hex.neighbour(5))
        val c6 = hexCentres[hex]!!

        return listOf(c0, c1, c2, c3, c4, c5, c6)
    }

    private fun averageCentres(h0: Hex, h1: Hex, h2: Hex): Vector3f {
        val hc0 = hexCentres.getOrDefault(h0, Vector3f())
        val hc1 = hexCentres.getOrDefault(h1, Vector3f())
        val hc2 = hexCentres.getOrDefault(h2, Vector3f())
        return hc0.add(hc1, Vector3f()).add(hc2).div(3f)
    }

    private fun calculateHexCentres() {
        knotCoordinates.forEachIndexed { segment, knotData ->
            val hexList = mutableListOf<Hex>()
            var hexToAdd = when {
                segment % 2 == 0 -> {
                    val hq = segment / 2
                    val hr = 0
                    val hs = -hq
                    hexGrid.hex(hq, hr, hs)
                }
                else -> {
                    val hq = (segment + 1) / 2
                    val hr = -1
                    val hs = -hq - hr
                    hexGrid.hex(hq, hr, hs)
                }
            }
            hexList.add(hexToAdd)
            (0 until (hexGrid.n / 2 - 1)).forEach { _ ->
                hexToAdd = hexToAdd.diagonalNeighbour(1) // add the "north" diagonal
                hexList.add(hexToAdd)
            }
            // Now distribute the hexes evenly around a circle adjusted from the current knotData
            // We will record only their centres, and later calculate their corners
            hexList.forEachIndexed { index, hex ->
                // pencil and paper!
                val adjustForPointy = if (segment%2 == 0) 0.0 else (1.0 / hexGrid.n)
                val theta = 2.0 * PI * (2.0 / hexGrid.n * index + adjustForPointy)
                val p = knotData.point
                val n = knotData.normal
                val t = knotData.tangent
                val b = t.cross(n, Vector3f())
                val xd = b.mul((r * cos(theta)).toFloat(), Vector3f())
                val yd = n.mul((r * sin(theta)).toFloat(), Vector3f())
                val centre = xd.add(p).add(yd)
                hexCentres[hex] = centre
            }
        }
    }
}