package net.fish.y2022

import net.fish.Day
import net.fish.collections.takeWhileInclusive
import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.resourceLines
import net.fish.y2021.GridDataUtils

object Day08 : Day {
    private val grid by lazy { toTreeGrid(resourceLines(2022, 8)) }

    override fun part1() = doPart1(grid)
    override fun part2() = doPart2(grid)

    fun doPart1(grid: TreeGrid): Int = grid.seen().size
    fun doPart2(grid: TreeGrid): Int = grid.maxScenicScore()

    fun toTreeGrid(data: List<String>): TreeGrid {
        return TreeGrid(GridDataUtils.mapIntPointsFromLines(data))
    }

    data class TreeGrid(private val gridData: Map<Point, Int>) {
        private val compassDirs = listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
        private val boundary: Pair<Point, Point> = gridData.keys.bounds()
        private fun onBoundary(p: Point): Boolean =
            p.x == boundary.first.x || p.x == boundary.second.x || p.y == boundary.first.y || p.y == boundary.second.y

        private fun allPoints(): Set<Point> = gridData.keys
        fun at(p: Point): Int = gridData[p] ?: throw Exception("Unknown point $p")

        fun bestScenicPoint(): Point = allPoints().maxBy { scenicScore(it) }
        fun maxScenicScore() = scenicScore(bestScenicPoint())
        fun scenicScore(p: Point): Int = compassDirs.fold(1) { ac, d ->
            ac * viewingDistance(p, d)
        }

        fun viewingDistance(p: Point, d: Direction): Int {
            if (onBoundary(p)) return 0
            return pointsToBoundaryInDir(p, d).takeWhileInclusive { at(it) < at(p) }.count()
        }

        fun seen(): List<Point> = allPoints().fold(mutableListOf()) { acc, point ->
            if (canSee(point)) acc += point
            acc
        }

        private fun canSee(p: Point): Boolean {
            if (onBoundary(p)) return true
            return compassDirs.any { d ->
                pointsToBoundaryInDir(p, d).all { at(it) < at(p) }
            }
        }

        private fun pointsToBoundaryInDir(p: Point, dir: Direction): Sequence<Point> {
            if (onBoundary(p)) return emptySequence()
            return generateSequence(p + dir) { it + dir }.takeWhileInclusive { !onBoundary(it) }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }
}
