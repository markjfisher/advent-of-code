package net.fish.y2023

import net.fish.Day
import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.resourceLines
import net.fish.y2021.GridDataUtils

object Day10 : Day {
    private val data by lazy { resourceLines(2023, 10) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val pipeMaze = parsePuzzle(data)
        return pipeMaze.findLoop().size / 2
    }
    fun doPart2(data: List<String>): Int {
        val pipeMaze = parsePuzzle(data)
        val reducedMaze = pipeMaze.removeNonLooped()
        return reducedMaze.countInside()
    }

    private val entryToExitMap = mapOf(
        Pair('|', Direction.SOUTH) to Direction.NORTH,
        Pair('|', Direction.NORTH) to Direction.SOUTH,
        Pair('-', Direction.WEST) to Direction.EAST,
        Pair('-', Direction.EAST) to Direction.WEST,
        Pair('L', Direction.NORTH) to Direction.EAST,
        Pair('L', Direction.EAST) to Direction.NORTH,
        Pair('J', Direction.NORTH) to Direction.WEST,
        Pair('J', Direction.WEST) to Direction.NORTH,
        Pair('7', Direction.SOUTH) to Direction.WEST,
        Pair('7', Direction.WEST) to Direction.SOUTH,
        Pair('F', Direction.SOUTH) to Direction.EAST,
        Pair('F', Direction.EAST) to Direction.SOUTH,
    )

    val directionsFor = mapOf(
        '|' to listOf(Pair('|', Direction.SOUTH), Pair('|', Direction.NORTH)),
        '-' to listOf(Pair('-', Direction.WEST), Pair('-', Direction.EAST)),
        'L' to listOf(Pair('L', Direction.NORTH), Pair('L', Direction.EAST)),
        'J' to listOf(Pair('J', Direction.NORTH), Pair('J', Direction.WEST)),
        '7' to listOf(Pair('7', Direction.SOUTH), Pair('7', Direction.WEST)),
        'F' to listOf(Pair('F', Direction.SOUTH), Pair('F', Direction.EAST)),
    )

    data class PipeMaze(val points: Map<Point, Char>) {
        fun findLoop(): List<Point> {
            tailrec fun walk(current: Point, dir: Direction, currentPath: MutableList<Point>): List<Point> {
                // println("@$current, going: $dir: $currentPath")

                val newLocation = current + dir
                // moving to a point that doesn't exist, or is a space
                if (!points.contains(newLocation) || points[newLocation] == '.') return emptyList()

                val newPipe = points[newLocation]!!
                // we looped
                if (newPipe == 'S') return currentPath

                // check if we walked around a loop that doesn't get to start - don't think this is possible as we have no T junction
                if (currentPath.contains(newLocation)) return emptyList()

                // does the next part have a piece that connects?
                val nextConnectedDir = entryToExitMap[Pair(newPipe, dir.opposite())] ?: return emptyList()

                // it's a valid, and connected point, so continue along it
                currentPath += newLocation
                // we need to use dir.opposite, as we come in from the opposite side to the direction we're moving
                return walk(newLocation, nextConnectedDir, currentPath)
            }

            val start = points.filter { it.value == 'S' }.keys.first()
            val loops = setOf('|', '-', 'L', 'J', '7', 'F').map { replaceChar -> walk(start, directionsFor[replaceChar]!!.first().second, mutableListOf(start)) }
            return loops.first { it.isNotEmpty() }
        }

        fun removeNonLooped(): PipeMaze {
            val loop = findLoop()
            // replace anything not part of the pipe with a '.'
            val newPoints = points.map { e ->
                if (loop.contains(e.key)) {
                    e.key to e.value
                } else {
                    e.key to '.'
                }
            }.toMap()
            return PipeMaze(newPoints)
        }

        fun countInside(): Int {
            val loop = findLoop()
            val dots = points.filter { it.value == '.' }.keys
            return dots.count { isPointInLoop(loop, it) }
        }

        private fun isPointInLoop(loop: List<Point>, point: Point): Boolean {
            var intersections = 0
            val outsidePoint = Point(-1, -1)

            for (i in loop.indices) {
                val start = loop[i]
                val end = if (i + 1 == loop.size) loop[0] else loop[i + 1]

                if (doLinesIntersect(start, end, point, outsidePoint)) {
                    intersections++
                }
            }
            val isInLoop = intersections %2 == 1
            // println("checking $point, r: $isInLoop, intersections = $intersections")

            return isInLoop
        }

        private fun doLinesIntersect(start: Point, end: Point, point: Point, outsidePoint: Point): Boolean {
            if (start.y <= point.y) {
                if (end.y > point.y && isPointOnRight(start, end, point, outsidePoint)) {
                    return true
                }
            } else if (end.y <= point.y) {
                if (isPointOnRight(start, end, point, outsidePoint)) {
                    return true
                }
            }

            return false
        }

        private fun isPointOnRight(start: Point, end: Point, point: Point, outsidePoint: Point): Boolean {
            val crossProduct1 = ((end.y - start.y) * (point.x - start.x)) - ((end.x - start.x) * (point.y - start.y))
            val crossProduct2 = ((end.y - start.y) * (outsidePoint.x - start.x)) - ((end.x - start.x) * (outsidePoint.y - start.y))
            return crossProduct1 * crossProduct2 < 0
        }
    }

    fun parsePuzzle(data: List<String>): PipeMaze {
        return PipeMaze(GridDataUtils.mapCharPointsFromLines(data))
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}