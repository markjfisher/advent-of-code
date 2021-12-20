package net.fish.geometry.hex

import net.fish.geometry.grid.Grid
import net.fish.geometry.hex.Orientation.ORIENTATION.FLAT
import net.fish.geometry.hex.Orientation.ORIENTATION.POINTY

/*
 This is a "rectangular" grid of Hex objects, whose ends wrap around.
 In order to wrap correctly, for a pointy based hex grid, the height must be even.
 For a flat based hex grid, the width must be even.
 */
data class WrappingHexGrid(
    var m: Int,
    var n: Int,
    var layout: Layout,
) : HexConstrainer, Grid {
    init {
        require(if (layout.orientation == POINTY) n % 2 == 0 else true) {
            "Invalid dimensions for pointy orientation, n must be even. Given: $n"
        }

        require(if (layout.orientation == FLAT) m % 2 == 0 else true) {
            "Invalid dimensions for flat orientation, m must be even. Given: $m"
        }
    }

    override fun simpleName(): String {
        return String.format("%s[m: %d, n: %d, l: %s]", this.javaClass.simpleName, m, n, layout.orientation)
    }

    override var width: Int = m
        set(value) {
            m = value
            field = value
        }

    override var height: Int = n
        set(value) {
            n = value
            field = value
        }

    // This is in terms of m/n, and needs to be multiplied by the layout's hex size to get an actual width/height
    fun hexWidth(): Double = when (layout.orientation) {
        POINTY -> m * layout.orientation.o.f0 // f0 = sqrt(3)
        FLAT -> 1.5 * m
    }

    fun hexHeight(): Double = when (layout.orientation) {
        POINTY -> 1.5 * n
        FLAT -> n * layout.orientation.o.f3 // f3 = sqrt(3)
    }

    // Magic constants for constraining to pointy grid
    private fun getMinPointyR() = 1 - n
    private fun getMaxPointyR() = 0
    private fun getMaxPointyQminusS() = 2 * m - 1
    private fun getMinPointyQminusS() = 0

    // Magic constants for constraining to flat grid
    private fun getMinFlatRminusS() = 1 - 2 * n
    private fun getMaxFlatRminusS() = 0
    private fun getMinFlatQ() = 0
    private fun getMaxFlatQ() = m - 1

    fun hex(q: Int, r: Int, s: Int): Hex = constrain(Hex(q, r, s, this))

    override tailrec fun constrain(hex: Hex): Hex {
        val adjust = when (layout.orientation) {
            POINTY -> adjust(hex.r, getMinPointyR(), getMaxPointyR(), Hex(-n / 2, n, -n / 2), hex.q, hex.s, getMinPointyQminusS(), getMaxPointyQminusS(), Hex(m, 0, -m))
            FLAT -> adjust(hex.q, getMinFlatQ(), getMaxFlatQ(), Hex(m, -m / 2, -m / 2), hex.r, hex.s, getMinFlatRminusS(), getMaxFlatRminusS(), Hex(0, n, -n))
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

    override fun items(): Iterable<Hex> {
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

}