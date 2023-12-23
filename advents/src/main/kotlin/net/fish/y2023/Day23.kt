package net.fish.y2023

import net.fish.Day
import net.fish.geometry.Direction.EAST
import net.fish.geometry.Direction.NORTH
import net.fish.geometry.Direction.SOUTH
import net.fish.geometry.Direction.WEST
import net.fish.geometry.Point
import net.fish.geometry.gridString
import net.fish.resourceLines
import net.fish.y2021.GridDataUtils

object Day23 : Day {
    private val data by lazy { resourceLines(2023, 23) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val iceGrid = toIceGrid(data)
        val paths = iceGrid.paths()
        return paths.maxOf { it.size } - 1
    }
    fun doPart2(data: List<String>): Int {
        val iceGrid = toIceGrid(data)
        val paths = iceGrid.paths(false)
        return paths.maxOf { it.size } - 1
    }

    data class IceGrid(val locations: Map<Point, Char>, val width: Int, val height: Int) {
        val start = Point(1,0)
        val end = Point(width - 2, height - 1)
        override fun toString() = locations.gridString()

        fun paths(useForced: Boolean = true): List<List<Point>> {
            val visited = mutableSetOf<Point>()
            val paths = mutableListOf<List<Point>>()
            val currentPath = mutableListOf<Point>()

            // this doesn't scale for P2 real data, only test data
            fun dfs(point: Point) {
                if (point == end) {
                    paths.add(ArrayList(currentPath))
                    return
                }

                val neighbours = point.neighbours()
                val forcedDirection = if (useForced) when (locations[point]) {
                    '>' -> point + EAST
                    'v' -> point + SOUTH
                    '<' -> point + WEST
                    '^' -> point + NORTH
                    else -> null
                } else null

                val pointsToVisit = forcedDirection?.let { listOf(it) } ?: neighbours
                for (nextPoint in pointsToVisit) {
                    if (nextPoint in locations.keys && locations[nextPoint] != '#' && nextPoint !in visited) {
                        visited.add(nextPoint)
                        currentPath.add(nextPoint)
                        dfs(nextPoint)
                        visited.remove(nextPoint)
                        currentPath.removeAt(currentPath.size - 1)
                    }
                }
            }

            visited.add(start)
            currentPath.add(start)
            dfs(start)

            return paths
        }
    }

    fun toIceGrid(data: List<String>): IceGrid {
        return IceGrid(GridDataUtils.mapCharPointsFromLines(data), data[0].length, data.size)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}