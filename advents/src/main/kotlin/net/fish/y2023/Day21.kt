package net.fish.y2023

import kotlin.math.max
import net.fish.Day
import net.fish.geometry.Point
import net.fish.resourceLines
import net.fish.y2021.GridDataUtils

object Day21 : Day {
    private val data by lazy { resourceLines(2023, 21) }

    override fun part1() = doPart1(data, 64)
    override fun part2() = doPart2(data, 26501365L)

    fun doPart1(data: List<String>, steps: Int): Int {
        val (points, start) = toWalkableGrid(data)

        // some debug, working out the diagonals, horizontal and vertical from starting are all empty in real data
//        val mainDiagonal = (0 until data.size).map { Point(it, it) }
//        val secondaryDiagonal = (0 until data.size).map { Point(it, data.size - 1 - it) }
//        val mainColumn = (0 until data.size).map { Point((data.size + 1) / 2, it)}
//        val mainRow = (0 until data.size).map { Point(it, (data.size + 1) / 2)}
//        assert(points.containsAll(mainDiagonal + secondaryDiagonal + mainColumn + mainRow))

        val within = reachablePoints(start, steps, points, data.size)
        val evenSteps = within.filter { it.manhattenDistance(start) % 2 == 0 }
        return evenSteps.count()
    }

    fun doPart2(data: List<String>, goal: Long): Long {
        // Solution to quadratic for given initial values from particulars of solution.
        // Kind of cheating, we've pre-calculated the parameters to the equation through brute force for 3 values. See countEvery() below
        fun f(vs: List<Long>, n: Long): Long {
            val b0 = vs[0]
            val b1 = vs[1] - vs[0]
            val b2 = vs[2] - vs[1]
            return b0 + b1 * n + (n * (n - 1) / 2L) * (b2 - b1)
        }

        // find the quadratic value after goal/size sequences
        // These are first 3 entries in finding the expanding count of points that can be reached from starting point
        // that match the condition the step count mod size == goal mod size
        return f(listOf(3791L, 33646L, 93223L), goal / data.size)
    }

    fun reachablePoints(start: Point, n: Int, points: Set<Point>, width: Int): Set<Point> {
        val visited = mutableSetOf<Point>()
        val queue = ArrayDeque<Pair<Point, Int>>()

        queue.addFirst(start to 0)

        // for debugging queue sizes
        var maxQueue = 0
        while (queue.isNotEmpty()) {
            val (currentPoint, steps) = queue.removeLast()
            if (currentPoint !in visited && currentPoint in points) {
                visited.add(currentPoint)
                if (steps < n) {
                    currentPoint.neighbours().forEach { neighbour ->
                        val newPoint = boundPoint(neighbour, width)
                        if (newPoint !in visited && newPoint in points) {
                            queue.addFirst(neighbour to steps + 1)
                            maxQueue = max(maxQueue, queue.size)
                        }
                    }
                }
            }
        }

        return visited
    }

    // Works out the factors needed for the geometric progression
    // Takes 4s to run, so left to provide if needed. Test just gets first value.
    fun countEvery(grid: Set<Point>, start: Point, mod: Int, maxValues: Int, width: Int): List<Int> {
        // move the points to neighbours in virtual grid that extends infinitely
        fun move(points: Set<Point>): Set<Point> {
            return points.flatMap { p->
                p.neighbours().mapNotNull { if (boundPoint(it, width) in grid) it else null }
            }.toSet()
        }

        var current = setOf(start)
        val returns = mutableListOf<Int>()
        var iteration = 0
        while (returns.size < maxValues) {
            iteration++
            current = move(current)
            if ((iteration % width) == mod) returns += current.size
        }
        return returns
    }

    private fun boundPoint(neighbour: Point, width: Int): Point {
        var boundNeighbourX = neighbour.x % width
        if (boundNeighbourX < 0) boundNeighbourX += width
        var boundNeighbourY = neighbour.y % width
        if (boundNeighbourY < 0) boundNeighbourY += width
        val newPoint = Point(boundNeighbourX, boundNeighbourY)
        return newPoint
    }

    fun toWalkableGrid(data: List<String>): Pair<Set<Point>, Point> {
        val gridMap = GridDataUtils.mapCharPointsFromLines(data)
        val startingPosition = gridMap.filter { it.value == 'S' }.keys.first()
        return Pair(gridMap.filter { it.value == '.' }.keys + startingPosition, startingPosition)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}