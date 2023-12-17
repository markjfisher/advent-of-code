package net.fish.y2023

import net.fish.Day
import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.geometry.contains
import net.fish.geometry.get
import net.fish.network.findShortestPathByPredicate
import net.fish.resourceLines
import net.fish.y2021.GridDataUtils

object Day17 : Day {
    private val data by lazy { resourceLines(2023, 17) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val grid2 = GridDataUtils.mapIntPointsFromLines(data)
        val start = PointInDir(Point(0, 0), Direction.EAST, 0)
        val end = Point(data[0].length - 1, data.size - 1)
        val path = findShortestPathByPredicate(
            start,
            { (p, _) -> p == end },
            { it.neighbours().filter { (n) -> n in grid2 }},
            { _, (p) -> grid2[p]!! }
        )
        return path.getScore()
    }
    fun doPart2(data: List<String>): Int = data.size

    data class PointInDir(val point: Point, val direction: Direction, val line: Int) {
        fun neighbours(): List<PointInDir> {
            return buildList {
                // can't go more than 3 in a straight line
                if (line < 3) {
                    add(PointInDir(point + direction, direction, line + 1))
                }
                // right
                add(PointInDir(point + direction.cw(), direction.cw(), 1))
                // left
                add(PointInDir(point + direction.ccw(), direction.ccw(), 1))
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}