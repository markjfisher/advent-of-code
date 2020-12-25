package net.fish

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.math.sqrt

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

data class Orientation(
    val f0: Double,
    val f1: Double,
    val f2: Double,
    val f3: Double,
    val b0: Double,
    val b1: Double,
    val b2: Double,
    val b3: Double,
    val start_angle: Double
)

data class Layout(
    val orientation: Orientation,
    val size: Point2D,
    val origin: Point2D
) {
    fun hexToPixel(h: Hex): Point2D {
        val (f0, f1, f2, f3) = orientation
        val x = (f0 * h.q + f1 * h.r) * size.x
        val y = (f2 * h.q + f3 * h.r) * size.y
        return Point2D(x + origin.x, y + origin.y)
    }

    fun pixelToHex(p: Point2D): FractionalHex {
        val (x, y) = Point2D((p.x - origin.x) / size.x, (p.y - origin.y) / size.y)
        val q = orientation.b0 * x + orientation.b1 * y
        val r = orientation.b2 * x + orientation.b3 * y
        return FractionalHex(q, r, -q - r)
    }

    fun hexCornerOffset(corner: Int): Point2D {
        val angle = 2.0 * Math.PI * (orientation.start_angle - corner) / 6.0
        return Point2D(size.x * cos(angle), size.y * sin(angle))
    }

    fun polygonCorners(h: Hex): List<Point2D> {
        val corners = mutableListOf<Point2D>()
        val (x, y) = hexToPixel(h)
        for (i in 0..5) {
            val (x1, y1) = hexCornerOffset(i)
            corners.add(Point2D(x + x1, y + y1))
        }
        return corners
    }

    companion object {
        var pointy = Orientation(sqrt(3.0), sqrt(3.0) / 2.0, 0.0, 3.0 / 2.0, sqrt(3.0) / 3.0, -1.0 / 3.0, 0.0, 2.0 / 3.0, 0.5)
        var flat = Orientation(3.0 / 2.0, 0.0, sqrt(3.0) / 2.0, sqrt(3.0), 2.0 / 3.0, 0.0, -1.0 / 3.0, sqrt(3.0) / 3.0, 0.0)
    }
}

data class Point2D(
    val x: Double,
    val y: Double
)
