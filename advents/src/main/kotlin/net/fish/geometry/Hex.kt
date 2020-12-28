package net.fish.geometry

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.roundToLong

// See https://www.redblobgames.com/grids/hexagons/implementation.html

data class Hex(
    val q: Int,
    val r: Int,
    val s: Int
) {
    init {
        require(q + r + s == 0) { "q + r + s must be 0" }
    }

    operator fun plus(other: Hex) = add(other)
    operator fun minus(other: Hex) = subtract(other)
    operator fun times(k: Int) = scale(k)

    fun add(other: Hex): Hex = Hex(q + other.q, r + other.r, s + other.s)
    fun subtract(other: Hex): Hex = Hex(q - other.q, r - other.r, s - other.s)
    fun scale(k: Int): Hex = Hex(q * k, r * k, s * k)
    fun rotateLeft(): Hex = Hex(-s, -q, -r)
    fun rotateRight(): Hex = Hex(-r, -s, -q)
    fun neighbour(d: Int) = this + direction(d)
    fun diagonalNeighbour(d: Int) = this + diagonalDirection(d)
    fun length(): Int = (abs(q) + abs(r) + abs(s)) / 2
    fun distance(other: Hex) = (this - other).length()

    companion object {
        val directions = listOf(Hex(1, 0, -1), Hex(1, -1, 0), Hex(0, -1, 1), Hex(-1, 0, 1), Hex(-1, 1, 0), Hex(0, 1, -1))
        val diagonals = listOf(Hex(2, -1, -1), Hex(1, -2, 1), Hex(-1, -1, 2), Hex(-2, 1, 1), Hex(-1, 2, -1), Hex(1, 1, -2))

        fun direction(d: Int) = directions[d]
        fun diagonalDirection(d: Int) = diagonals[d]
    }
}

data class FractionalHex(
    val q: Double,
    val r: Double,
    val s: Double
) {
    init {
        require((q + r + s).roundToLong() == 0L) { "q + r + s must be 0" }
    }

    fun hexRound(): Hex {
        var qi = q.roundToInt()
        var ri = r.roundToInt()
        var si = s.roundToInt()
        val qDiff = abs(qi - q)
        val rDiff = abs(ri - r)
        val sDiff = abs(si - s)
        if (qDiff > rDiff && qDiff > sDiff) {
            qi = -ri - si
        } else if (rDiff > sDiff) {
            ri = -qi - si
        } else {
            si = -qi - ri
        }
        return Hex(qi, ri, si)
    }

    fun hexLerp(b: FractionalHex, t: Double): FractionalHex {
        return FractionalHex(q * (1.0 - t) + b.q * t, r * (1.0 - t) + b.r * t, s * (1.0 - t) + b.s * t)
    }

    companion object {
        fun hexLinedraw(a: Hex, b: Hex): List<Hex> {
            val n = a.distance(b)
            val aNudge = FractionalHex(a.q + 1e-06, a.r + 1e-06, a.s - 2e-06)
            val bNudge = FractionalHex(b.q + 1e-06, b.r + 1e-06, b.s - 2e-06)
            val results = mutableListOf<Hex>()
            val step = 1.0 / max(n, 1)
            for (i in 0..n) {
                results.add(aNudge.hexLerp(bNudge, step * i).hexRound())
            }
            return results.toList()
        }
    }
}

data class DoubledCoord(
    val col: Int,
    val row: Int
) {
    fun qdoubledToCube(): Hex {
        val q = col
        val r = ((row - col) / 2)
        val s = -q - r
        return Hex(q, r, s)
    }

    fun rdoubledToCube(): Hex {
        val q = ((col - row) / 2)
        val r = row
        val s = -q - r
        return Hex(q, r, s)
    }

    companion object {
        fun qdoubledFromCube(h: Hex): DoubledCoord {
            val col = h.q
            val row = 2 * h.r + h.q
            return DoubledCoord(col, row)
        }

        fun rdoubledFromCube(h: Hex): DoubledCoord {
            val col = 2 * h.q + h.r
            val row = h.r
            return DoubledCoord(col, row)
        }
    }
}


data class OffsetCoord(
    val col: Int,
    val row: Int
) {
    companion object {
        var EVEN = 1
        var ODD = -1

        fun qoffsetFromCube(offset: Int, h: Hex): OffsetCoord {
            val col = h.q
            val row = h.r + ((h.q + offset * (h.q and 1)) / 2)
            require(!(offset != EVEN && offset != ODD)) { "offset must be EVEN (+1) or ODD (-1)" }
            return OffsetCoord(col, row)
        }

        fun qoffsetToCube(offset: Int, h: OffsetCoord): Hex {
            val q = h.col
            val r = h.row - ((h.col + offset * (h.col and 1)) / 2)
            val s = -q - r
            require(!(offset != EVEN && offset != ODD)) { "offset must be EVEN (+1) or ODD (-1)" }
            return Hex(q, r, s)
        }

        fun roffsetFromCube(offset: Int, h: Hex): OffsetCoord {
            val col = h.q + ((h.r + offset * (h.r and 1)) / 2)
            val row = h.r
            require(!(offset != EVEN && offset != ODD)) { "offset must be EVEN (+1) or ODD (-1)" }
            return OffsetCoord(col, row)
        }

        fun roffsetToCube(offset: Int, h: OffsetCoord): Hex {
            val q = h.col - ((h.row + offset * (h.row and 1)) / 2)
            val r = h.row
            val s = -q - r
            require(!(offset != EVEN && offset != ODD)) { "offset must be EVEN (+1) or ODD (-1)" }
            return Hex(q, r, s)
        }
    }
}

