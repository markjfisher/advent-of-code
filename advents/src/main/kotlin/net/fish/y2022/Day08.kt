package net.fish.y2022

import net.fish.Day
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
    fun doPart2(grid: TreeGrid): Long = grid.maxScenicScore()

    fun toTreeGrid(data: List<String>): TreeGrid {
        return TreeGrid(GridDataUtils.mapIntPointsFromLines(data))
    }

    data class TreeGrid(private val gridData: Map<Point, Int>) {
        private val boundary: Pair<Point, Point> = gridData.keys.bounds()
        private fun onBoundary(p: Point): Boolean = p.x == boundary.first.x || p.x == boundary.second.x || p.y == boundary.first.y || p.y == boundary.second.y
        fun at(p: Point): Int = gridData[p] ?: throw Exception("Unknown point $p")
        private fun allPoints(): Set<Point> = gridData.keys

        fun bestScenicPoint(): Point = allPoints().maxBy {
            if (onBoundary(it)) 0L else {
                scenicScore(it)
            }
        }

        fun maxScenicScore() = scenicScore(bestScenicPoint())
        fun scenicScore(p: Point): Long = viewingDistance(p, Direction.NORTH) * viewingDistance(p, Direction.EAST) * viewingDistance(p, Direction.SOUTH) * viewingDistance(p, Direction.WEST)

        fun viewingDistance(p: Point, d: Direction): Long {
            if (onBoundary(p)) return 0L
            val pointsToEdge = pointsToBoundaryInDir(p, d)
            var c = 0L
            var keepGoing = true
            pointsToEdge.forEach {
                if (keepGoing) c++
                if (at(it) >= at(p)) keepGoing = false
            }
            return c
        }

        fun seen(): List<Point> = allPoints().fold(mutableListOf()) { acc, point ->
            if (canSee(point)) acc += point
            acc
        }

        private fun canSee(p: Point): Boolean {
            if (onBoundary(p)) return true
            return listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST).any { d ->
                pointsToBoundaryInDir(p, d).all { at(it) < at(p) }
            }
        }

        private fun pointsToBoundaryInDir(p: Point, dir: Direction): List<Point> {
            if (onBoundary(p)) return emptyList()

            val accumulated = mutableListOf<Point>()
            var foundEdge = false
            var newP = p + dir
            while(!foundEdge) {
                accumulated += newP
                if (onBoundary(newP)) foundEdge = true
                newP += dir
            }
            return accumulated.toList()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}