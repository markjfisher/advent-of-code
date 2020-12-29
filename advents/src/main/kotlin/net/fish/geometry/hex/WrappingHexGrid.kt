package net.fish.geometry.hex

import net.fish.geometry.hex.Orientation.ORIENTATION.FLAT
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY
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
): HexConstrainer {
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

    fun height(): Double = when(layout.orientation) {
        POINTY -> 1.5 * n
        FLAT -> n * layout.orientation.o.f3 // f3 = sqrt(3)
    }

    // Magic constants for constraining to pointy grid
    private val minPointyR = 1 - n
    private val maxPointyR = 0
    private val maxPointyQminusS = 2 * m - 1
    private val minPointyQminusS = 0
    private val pointyROffset = Hex(-n / 2, n, -n / 2)
    private val pointyQSOffset = Hex(m, 0, -m)

    // Magic constants for constraining to flat grid
    private val minFlatRminusS = 1 - 2 * n
    private val maxFlatRminusS = 0
    private val minFlatQ = 0
    private val maxFlatQ = m - 1
    private val flatQOffset = Hex(m, -m / 2, -m / 2)
    private val flatRSOffset = Hex(0, n, -n)

    fun hex(q: Int, r: Int, s: Int): Hex = constrain(Hex(q, r, s, this))

    override tailrec fun constrain(hex: Hex): Hex {
        val adjust = when (layout.orientation) {
            POINTY -> adjust(hex.r, minPointyR, maxPointyR, pointyROffset, hex.q, hex.s, minPointyQminusS, maxPointyQminusS, pointyQSOffset)
            FLAT -> adjust(hex.q, minFlatQ, maxFlatQ, flatQOffset, hex.r, hex.s, minFlatRminusS, maxFlatRminusS, flatRSOffset)
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

        return layout.polygonCorners(hex).map {
            println("doing hex corner at $it")
            val theta = 2.0 * PI * (1.0 - it.x / width())
            val phi = 2.0 * PI * it.y / height()

            q(theta, phi)
        }
    }
}