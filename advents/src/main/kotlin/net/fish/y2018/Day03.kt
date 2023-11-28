package net.fish.y2018

import net.fish.Day
import net.fish.geometry.Point
import net.fish.resourceLines
import java.lang.Exception

object Day03 : Day {
    private val claimExtractor by lazy { Regex("""#(\d+) @ (\d+),(\d+): (\d+)x(\d+)""") }
    private val data by lazy { toClaims(resourceLines(2018, 3)) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    data class Claim(val id:Int, val x: Int, val y: Int, val sx: Int, val sy: Int) {
        fun allPoints(): List<Point> {
            val combinations: MutableList<Point> = mutableListOf()
            for (a in (x until (x+sx))) {
                for (b in (y until (y+sy))) {
                    combinations.add(Point(a, b))
                }
            }
            return combinations.toList()
        }
    }

    fun toClaims(data: List<String>): List<Claim> = data.map { line ->
        claimExtractor.find(line)?.destructured!!.let { (id, x, y, sx, sy) ->
            // println("Adding claim for $id, $x, $y, $sx, $sy")
            Claim(id.toInt(), x.toInt(), y.toInt(), sx.toInt(), sy.toInt())
        }
    }

    fun doPart1(claims: List<Claim>): Int = createPointToClaims(claims).values.count { it.size > 1 }

    private fun createPointToClaims(claims: List<Claim>): MutableMap<Point, MutableSet<Claim>> {
        val mapPointToClaims: MutableMap<Point, MutableSet<Claim>> = mutableMapOf()
        claims.forEach { claim ->
            claim.allPoints().forEach { p ->
                mapPointToClaims.getOrPut(p) { mutableSetOf() } += claim
            }
        }
        return mapPointToClaims
    }

    fun doPart2(claims: List<Claim>): Int {
        // find all claims that have a point in more than one claim, and remove them from all claims. should be left with 1 claim that
        // doesn't overlap anything.
        val p2c = createPointToClaims(claims)
        val claimsThatOverlap = p2c.values.filter { it.size > 1 }.flatten().toSet()
        val unique = claims - claimsThatOverlap
        if (unique.size != 1) {
            throw Exception("Found more than 1 that doesn't overlap: $unique")
        }
        return unique.first().id
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}