package net.fish.geometry

import net.fish.geometry.Orientation.ORIENTATION.FLAT
import net.fish.geometry.Orientation.ORIENTATION.POINTY

/*
 This is a rectangular grid of Hex objects, whose ends wrap around.
 Thus making a Toroid in topology.
 This class required a lot of sweat, new ink for hex graphs, 2 pencils, 1 eraser, and quite a bit of wine.
 */
data class WrappingHexGrid(
    val m: Int,
    val n: Int,
    val orientation: Orientation.ORIENTATION
) {
    init {
        require(if (orientation == POINTY) n % 2 == 0 else true) {
            "Invalid dimensions for pointy orientation, n must be even. Given: $n"
        }

        require(if (orientation == FLAT) m % 2 == 0 else true) {
            "Invalid dimensions for flat orientation, m must be even. Given: $m"
        }
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

    tailrec fun constrainToGrid(hex: Hex): Hex {
        val adjust = when (orientation) {
            POINTY -> adjust(hex.r, minPointyR, maxPointyR, pointyROffset, hex.q, hex.s, minPointyQminusS, maxPointyQminusS, pointyQSOffset)
            FLAT -> adjust(hex.q, minFlatQ, maxFlatQ, flatQOffset, hex.r, hex.s, minFlatRminusS, maxFlatRminusS, flatRSOffset)
        }
        return if (adjust == Hex(0, 0, 0)) hex else constrainToGrid(hex + adjust)
    }

    private fun adjust(hA: Int, minA: Int, maxA: Int, offsetA: Hex, hB: Int, hC: Int, minBC: Int, maxBC: Int, offsetBC: Hex): Hex {
        var adjust = Hex(0, 0, 0)
        // hA is the hex component that is purely horizontal or vertical (depending on grid orientation)
        // e.g. for pointy, this is hex.r, for flat this is hex.q
        if (hA < minA) adjust += offsetA
        if (hA > maxA) adjust -= offsetA
        // hB/hC are the non- horizontal/vertical components which when subtracted give perpendicular vector to hA
        if (hB - hC < minBC) adjust += offsetBC
        if (hB - hC > maxBC) adjust -= offsetBC
        return adjust
    }

    fun neighbours(hex: Hex): List<Hex> = Hex.directions.map { constrainToGrid(it + hex) }
    fun diagonals(hex: Hex): List<Hex> = Hex.diagonals.map{ constrainToGrid(it + hex) }
}