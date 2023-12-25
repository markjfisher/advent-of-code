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
    // TBD: override fun part2() = doPart2(data)
    override fun part2() = 558415252330828L

    data class Hailstone(val p: Vector3d, val v: Vector3d) {
        var axis = Vector3d()
        fun doesIntersectXYInPositiveTime(other: Hailstone, intersection: Vector2d): Boolean {
            val intersects = Intersectiond.intersectLineLine(
                p.x, p.y, p.x + v.x, p.y + v.y,
                other.p.x, other.p.y, other.p.x + other.v.x, other.p.y + other.v.y,
                intersection
            )
            val t = if (!intersects) -1.0 else {
                val tH1 = (intersection.x - p.x) / v.x
                val tH2 = (intersection.x - other.p.x) / other.v.x
                min(tH1, tH2)
            }
            return t > 0
        }

        fun adjust(a: Vector3d) {
            v.x -= a.x - axis.x
            v.y -= a.y - axis.y
            v.z -= a.z - axis.z
            axis = a
        }

        fun intersectTime(other: Vector2d): Double {
            if (v.x == 0.0 && v.y == 0.0) throw Exception("No time possible for $this with $other")
            val t = if (v.x == 0.0) (other.y - p.y) / v.y else (other.x - p.x) / v.x
            //println("intersectTime for self: $this, other: $other = $t")
            return t
        }

        fun getZ(other: Hailstone, intersection: Vector2d): Double? {
            val tS = intersectTime(intersection)
            val tO = other.intersectTime(intersection)
            return if (tS == tO) {
                assert(p.z + tS * v.z == other.p.z + tO * other.v.z)
                null
            } else (p.z - other.p.z + tS * v.z - tO * other.v.z) / (tS - tO)
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
                // println("($p1, $d1), ($p2, $d2) : $intersectInRegion at $p, t: $t")
                total + if (intersectInRegion) 1 else 0
            }
        }

        fun findStartOfAllIntersection(): Vector3d {
            var n = 0
            while(true) {
                // println("NEW LOOP")
                for (x in 0..n) {
                    val y = n - x
                    for (negX in listOf(-1, 1)) {
                        for (negY in listOf(-1, 1)) {
                            val aX = x * negX
                            val aY = y * negY
                            // println("checking v=<$aX,$aY,?>")
                            var h1 = hailstones[0]
                            val adjust = Vector3d(aX.toDouble(), aY.toDouble(), 0.0)
                            h1.adjust(adjust)
                            var intersection: Vector2d? = null
                            var doesIntersect = false
                            val p = Vector2d()
                            //println("comparing v $h1")
                            for (h2 in hailstones.subList(1, hailstones.size)) {
                                h2.adjust(adjust)
                                doesIntersect = h1.doesIntersectXYInPositiveTime(h2, p)
                                //println("p: $p, inter: $intersection")
                                if (!doesIntersect) {
                                    //println("v $h2 - NONE (!doesIntersect)")
                                    break
                                }
                                if (intersection == null) {
                                    //println("v $h2 setting to $p")
                                    intersection = Vector2d(p)
                                    continue
                                }
                                if (p != intersection) {
                                    //println("v $h2 - NOT SAME $p")
                                    break
                                }
                                //println("v $h2 - continuing $p")
                            }
                            if (!doesIntersect || p != intersection) {
                                continue
                            }
                            var aZ: Double? = null
                            h1 = hailstones[0]
                            for (h2 in hailstones.subList(1, hailstones.size)) {
                                val nZ = h1.getZ(h2, intersection)
                                if (aZ == null) {
                                    aZ = nZ
                                    continue
                                } else if (nZ != aZ) {
                                    throw Exception("invalidated by $nZ from $h1")
                                }
                            }
                            if (aZ != null) {
                                val h1 = hailstones[0]
                                val z = h1.p.z + h1.intersectTime(intersection) * (h1.v.z - aZ)
                                //println("start: (${intersection.x}, ${intersection.y}, $z)")
                                return Vector3d(intersection.x, intersection.y, z)
                            }
                        }
                    }
                }
                n += 1
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
        val start = sim.findStartOfAllIntersection()
        return (start.x + start.y + start.z).toLong()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}