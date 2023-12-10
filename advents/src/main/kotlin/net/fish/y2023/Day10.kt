package net.fish.y2023

import net.fish.Day
import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.resourceLines
import net.fish.y2021.GridDataUtils

object Day10 : Day {
    private val data by lazy { resourceLines(2023, 10) }

    // shave some time off by knowing where S starts and having replaced it with correct character
    override fun part1() = doPart1(data, Point(109, 76), false)
    override fun part2() = doPart2(data, Point(109, 76), false)

    fun doPart1(data: List<String>, start: Point? = null, tryPipes: Boolean = true): Int {
        val pipeMaze = parsePuzzle(data)
        return pipeMaze.findLoop(start, tryPipes).size / 2
    }
    fun doPart2(data: List<String>, start: Point? = null, tryPipes: Boolean = true): Int {
        val pipeMaze = parsePuzzle(data)
        val reducedMaze = pipeMaze.removeNonLooped(start, tryPipes)
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
        val loop = mutableListOf<Point>()
        fun findLoop(useStart: Point? = null, tryPipes: Boolean = true): List<Point> {
            val start = useStart ?: points.filter { it.value == 'S' }.keys.first()
            tailrec fun walk(startingPipe: Char, current: Point, dir: Direction, currentPath: MutableList<Point>): List<Point> {
                val newLocation = current + dir

                // moving to a point that doesn't exist, or is a space
                val newPipe = points[newLocation]
                if (newPipe == null || newPipe == '.') {
                    return emptyList()
                }

                // does the next part have a piece that connects?
                // we need to use dir.opposite, as we come in from the opposite side to the direction we're moving
                val nextConnectedDir = entryToExitMap[Pair(if(newPipe == 'S') startingPipe else newPipe, dir.opposite())]//  ?: return emptyList()
                if (nextConnectedDir == null) {
                    return emptyList()
                }
                if (newLocation == start) {
                    return currentPath
                }

                // it's a valid, and connected point, so continue along it
                currentPath += newLocation
                return walk(startingPipe, newLocation, nextConnectedDir, currentPath)
            }

            if (tryPipes) {
                val loops = setOf('|', '-', 'L', 'J', '7', 'F').map { replaceChar ->
                    walk(replaceChar, start, directionsFor[replaceChar]!!.first().second, mutableListOf(start))
                }
                return loops.first { it.isNotEmpty() }
            } else {
                val startPipe = points[useStart!!]!!
                return walk(startPipe, useStart, directionsFor[startPipe]!!.first().second, mutableListOf(useStart))
            }
        }

        fun removeNonLooped(useStart: Point? = null, tryPipes: Boolean = true): PipeMaze {
            val loop = findLoop(useStart, tryPipes)
            // replace anything not part of the pipe with a '.'
            val newPoints = points.map { e ->
                if (loop.contains(e.key)) {
                    e.key to e.value
                } else {
                    e.key to '.'
                }
            }.toMap()
            val newMaze = PipeMaze(newPoints)
            newMaze.loop.addAll(loop)
            return newMaze
        }

        fun countInside(): Int {
            // val loop = findLoop()
            val dots = points.filter { it.value == '.' }.keys
            // optimization: we only need turning points, as the segments are same if the next point is same horizontally or vertically.
            val turningPoints = loop.filter { points[it] != '-' && points[it] != '|' }
            return dots.count { isPointInLoop(turningPoints, it) }
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