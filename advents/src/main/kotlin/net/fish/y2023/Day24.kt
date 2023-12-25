package net.fish.y2023

import kotlin.math.min
import net.fish.Day
import net.fish.maths.combinations
import net.fish.maths.eq
import net.fish.maths.minus
import net.fish.maths.plus
import net.fish.maths.times
import net.fish.maths.z3
import net.fish.resourceLines
import org.joml.Intersectiond
import org.joml.Vector2d
import org.joml.Vector3d

object Day24 : Day {
    private val hailstoneExtractor by lazy { Regex("""(\d+),\s+(\d+),\s+(\d+)\s+@\s+(-?\d+),\s+(-?\d+),\s+(-?\d+)""")}
    private val data by lazy { resourceLines(2023, 24) }

    override fun part1() = doPart1(data, 200000000000000L, 400000000000000L)
    override fun part2() = 558415252330828L // takes a couple of seconds: doPart2(data)

    data class Hailstone(val p: Vector3d, val v: Vector3d) {
        fun doesIntersectXYInPositiveTime(other: Hailstone, intersection: Vector2d): Boolean {
            val intersects = Intersectiond.intersectLineLine(
                p.x, p.y, p.x + v.x, p.y + v.y,
                other.p.x, other.p.y, other.p.x + other.v.x, other.p.y + other.v.y,
                intersection
            )
            return if (!intersects) false else {
                val tH1 = (intersection.x - p.x) / v.x
                val tH2 = (intersection.x - other.p.x) / other.v.x
                min(tH1, tH2) > 0
            }
        }

        override fun toString(): String {
            return "<${p.x.toInt()}, ${p.y.toInt()}, ${p.z.toInt()} @ ${v.x.toInt()}, ${v.y.toInt()}, ${v.z.toInt()}>"
        }
    }

    data class HailstoneSim(val hailstones: List<Hailstone>) {
        fun countIntersectingXYIn(minXY: Long, maxXY: Long): Int {
            return hailstones.combinations(2).fold(0) { total, pair ->
                val intersection = Vector2d()
                val t = pair[0].doesIntersectXYInPositiveTime(pair[1], intersection)
                val intersectInRegion = t && (intersection.x >= minXY && intersection.x <= maxXY) && (intersection.y >= minXY && intersection.y <= maxXY)
                total + if (intersectInRegion) 1 else 0
            }
        }

    }

    fun toHailstoneSimulator(data: List<String>): HailstoneSim {
        val hailstones = data.map { line ->
            hailstoneExtractor.find(line)?.destructured!!.let { (ixs, iys, izs, dxs, dys, dzs) ->
                Hailstone(Vector3d(ixs.toDouble(), iys.toDouble(), izs.toDouble()), Vector3d(dxs.toDouble(), dys.toDouble(), dzs.toDouble()))
            }
        }
        return HailstoneSim(hailstones)
    }

    fun doPart1(data: List<String>, minXY: Long, maxXY: Long): Int {
        val sim = toHailstoneSimulator(data)
        return sim.countIntersectingXYIn(minXY, maxXY)
    }
    fun doPart2(data: List<String>): Long {
        val sim = toHailstoneSimulator(data)
        return z3 {
            val x_t = int("x_t")
            val y_t = int("y_t")
            val z_t = int("z_t")
            val xvel_t = int("xvel_t")
            val yvel_t = int("yvel_t")
            val zvel_t = int("zvel_t")
            val dt1 = int("dt1")
            val dt2 = int("dt2")
            val dt3 = int("dt3")

            val dt = listOf(dt1, dt2, dt3)


            val eqs = sim.hailstones.take(3).flatMapIndexed { idx, hs ->
                listOf(
                    (x_t - hs.p.x.toLong()) eq (dt[idx] * (hs.v.x.toLong() - xvel_t)),
                    (y_t - hs.p.y.toLong()) eq (dt[idx] * (hs.v.y.toLong() - yvel_t)),
                    (z_t - hs.p.z.toLong()) eq (dt[idx] * (hs.v.z.toLong() - zvel_t)),
                )
            }

            solve(eqs)

            eval(x_t + y_t + z_t).toLong()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}