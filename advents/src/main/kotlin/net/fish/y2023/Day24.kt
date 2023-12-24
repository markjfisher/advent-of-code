package net.fish.y2023

import kotlin.math.min
import net.fish.Day
import net.fish.maths.combinations
import net.fish.resourceLines
import org.joml.Intersectiond
import org.joml.Vector2d
import org.joml.Vector3d

object Day24 : Day {
    private val hailstoneExtractor by lazy { Regex("""(\d+),\s+(\d+),\s+(\d+)\s+@\s+(-?\d+),\s+(-?\d+),\s+(-?\d+)""")}
    private val data by lazy { resourceLines(2023, 24) }

    override fun part1() = doPart1(data, 200000000000000L, 400000000000000L)
    override fun part2() = doPart2(data)

    data class Hailstone(val p: Vector3d, val d: Vector3d) {
        fun doesIntersectXY(other: Hailstone, intersection: Vector2d): Boolean = Intersectiond.intersectLineLine(
            p.x, p.y, p.x + d.x, p.y + d.y,
            other.p.x, other.p.y, other.p.x + other.d.x, other.p.y + other.d.y,
            intersection
        )
    }

    data class HailstoneSim(val hailstones: List<Pair<Vector3d, Vector3d>>) {
        fun countIntersectingXYIn(minXY: Long, maxXY: Long): Int {
            return hailstones.combinations(2).fold(0) { total, pair ->
                val p = Vector2d()
                val p1 = pair[0].first
                val d1 = pair[0].second
                val p2 = pair[1].first
                val d2 = pair[1].second
                val intersect = Intersectiond.intersectLineLine(
                    p1.x, p1.y, p1.x + d1.x, p1.y + d1.y,
                    p2.x, p2.y, p2.x + d2.x, p2.y + d2.y,
                    p
                )
                // find the t for which the intersect point happened for both hailstones, take the earliest
                val t = if (!intersect) -1.0 else {
                    // p = a + tv
                    // t = (p.x - a.x) / v.x
                    val tP1 = (p.x - p1.x) / d1.x
                    val tP2 = (p.x - p2.x) / d2.x
                    min(tP1, tP2)
                }
                val intersectInRegion = (t > 0) && (p.x >= minXY && p.x <= maxXY) && (p.y >= minXY && p.y <= maxXY)
                // println("($p1, $d1), ($p2, $d2) : $intersectInRegion at $p, t: $t")
                total + if (intersectInRegion) 1 else 0
            }
        }
    }

    fun toHailstoneSimulator(data: List<String>): HailstoneSim {
        val hailstones = data.map { line ->
            hailstoneExtractor.find(line)?.destructured!!.let { (ixs, iys, izs, dxs, dys, dzs) ->
                Pair(Vector3d(ixs.toDouble(), iys.toDouble(), izs.toDouble()), Vector3d(dxs.toDouble(), dys.toDouble(), dzs.toDouble()))
            }
        }
        return HailstoneSim(hailstones)
    }

    fun doPart1(data: List<String>, minXY: Long, maxXY: Long): Int {
        val sim = toHailstoneSimulator(data)
        return sim.countIntersectingXYIn(minXY, maxXY)
    }
    fun doPart2(data: List<String>): Int = data.size

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}