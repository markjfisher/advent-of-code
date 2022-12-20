package net.fish.y2021

import net.fish.Day
import net.fish.geometry.Point
import net.fish.resourceLines
import kotlin.math.abs
import kotlin.math.sign

object Day05 : Day {
    private val vectorExtractor by lazy { Regex("""(\d+),(\d+) -> (\d+),(\d+)""") }
    private val data by lazy { toThermalVectors(resourceLines(2021, 5)) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(vs: List<ThermalVector>): Int = pointsWithCountAtLeast(vs, 2, false)
    fun doPart2(vs: List<ThermalVector>): Int = pointsWithCountAtLeast(vs, 2, true)

    private fun pointsWithCountAtLeast(vs: List<ThermalVector>, atLeast: Int, includeDiagonals: Boolean): Int {
        val grid = Grid()
        vs.forEach { v ->
            v.allPointsOnVector(includeDiagonals).forEach { p ->
                grid.addPoint(p)
            }
        }
        return grid.pointsWithCountAtLeast(atLeast)
    }

    fun toThermalVectors(lines: List<String>): List<ThermalVector> = lines.map { line ->
        vectorExtractor.find(line)?.destructured!!.let { (x1, y1, x2, y2) -> ThermalVector(Point(x1.toInt(), y1.toInt()), Point(x2.toInt(), y2.toInt())) }
    }

    class Grid {
        private val pointToCount: MutableMap<Point, Int> = mutableMapOf()
        fun addPoint(p: Point) {
            if (pointToCount[p] == null) pointToCount[p] = 1 else {
                val old = pointToCount[p]!!
                pointToCount[p] = old + 1
            }
        }
        fun pointsWithCountAtLeast(count: Int): Int {
            return pointToCount.count { it.value >= count }
        }
    }

    data class ThermalVector(
        val start: Point,
        val end: Point
    ) {
        fun allPointsOnVector(includeDiagonals: Boolean = false): List<Point> {
            return when {
                this.start.x == this.end.x && this.start.y == this.end.y -> listOf(Point(this.start.x, this.start.y))
                this.start.x == this.end.x -> walkVertical()
                this.start.y == this.end.y -> walkHorizontal()
                includeDiagonals && (abs(this.start.y - this.end.y) == abs(this.start.x - this.end.x)) -> walkDiagonal()
                else -> emptyList()
            }
        }

        private fun walkHorizontal(): List<Point> {
            val maxX = maxOf(this.start.x, this.end.x)
            val minX = minOf(this.start.x, this.end.x)
            return (minX .. maxX).map { Point(it, this.start.y) }
        }

        private fun walkVertical(): List<Point> {
            val maxY = maxOf(this.start.y, this.end.y)
            val minY = minOf(this.start.y, this.end.y)
            return (minY .. maxY).map { Point(this.start.x, it) }
        }

        fun walkDiagonal(): List<Point> {
            val numPoints = abs(this.start.x - this.end.x)
            return (0 .. numPoints).map { i ->
                this.start.plus(i * sign(this.end.x - this.start.x), i * sign(this.end.y - this.start.y))
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}

fun sign(x: Int): Int {
    return when {
        x < 0 -> -1
        x > 0 -> 1
        else -> 0
    }
}