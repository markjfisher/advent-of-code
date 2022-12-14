package net.fish.y2022

import net.fish.Day
import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.resourceLines
import kotlin.math.min

object Day14 : Day {
    private val entryPoint = Point(500, 0)

    override fun part1() = doPart1(toWallPoints(resourceLines(2022, 14)))
    override fun part2() = doPart2(toWallPoints(resourceLines(2022, 14)))

    fun doPart1(wallPoints: Set<Point>): Int {
        val simulator = runSimulator(wallPoints, false)
        return simulator.allPoints.size - simulator.wall.size
    }

    fun doPart2(wallPoints: Set<Point>): Int {
        val boundary = wallPoints.bounds()
        // we need an additional set of points for the lower floor, which is twice as wide as it is high + 1
        val lowerFloorY = boundary.second.y + 2
        val minLowerFloorX = entryPoint.x - lowerFloorY
        val maxLowerFloorX = entryPoint.x + lowerFloorY
        val newWall = wallPoints + (minLowerFloorX .. maxLowerFloorX).map { Point(it, lowerFloorY) }.toSet()
        val simulator = runSimulator(newWall, false)
        return simulator.allPoints.size - simulator.wall.size
    }

    private fun runSimulator(wallPoints: Set<Point>, showGrid: Boolean = false): SandSimulator {
        val simulator = SandSimulator(wallPoints)
        if (showGrid) println(simulator.grid().joinToString("\n"))
        do {
            val inserted = simulator.step()
        } while (inserted)
        if (showGrid) {
            println()
            println(simulator.grid().joinToString("\n"))
        }
        return simulator
    }

    fun toWallPoints(data: List<String>): Set<Point> {
        fun getWallPoints(corners: List<Point>): Set<Point> {
            val allPoints = mutableSetOf<Point>()
            (0 until corners.size - 1).forEach { i ->
                val bounds = listOf(corners[i], corners[i + 1]).bounds()
                when {
                    bounds.first == bounds.second -> allPoints.add(Point(bounds.first.x, bounds.first.y))
                    bounds.first.x == bounds.second.x -> (bounds.first.y .. bounds.second.y).forEach { allPoints.add(Point(bounds.first.x, it)) }
                    bounds.first.y == bounds.second.y -> (bounds.first.x .. bounds.second.x).forEach { allPoints.add(Point(it, bounds.first.y)) }
                    else -> throw Exception("Non horizontal/vertical line detected from: ${corners[i]}, to: ${corners[i+1]}")
                }
            }
            return allPoints.toSet()
        }

        return data.fold(setOf()) { ac, input ->
            ac + getWallPoints(
                input.split(" -> ")
                    .map { it.split(",", limit = 2) }
                    .map { (x, y) -> Point(x.toInt(), y.toInt()) }
            )

        }
    }

    data class SandSimulator(val wall: Set<Point>, val allPoints: MutableSet<Point> = wall.toMutableSet()) {
        private val boundary = wall.bounds().let { b ->
            // bring the y boundary up to 0 if not already there
            Pair(Point(b.first.x, min(0, b.first.y)), b.second)
        }

        // returns if a new state changed or not
        fun step(steps: Int = 1): Boolean {
            if (allPoints.contains(entryPoint)) return false

            var canContinue = true
            for (s in 0 until steps) {
                canContinue = moveSand(entryPoint)
                if (!canContinue) break
            }
            return canContinue
        }

        // returns if a new sand item settled in grid (true) else it ran off edge (false), and adds to the sand if it did settle (mutates Simulation state)
        private fun moveSand(p: Point): Boolean {
            // TEST SOUTH
            val sPoint = p + Direction.SOUTH
            if (!sPoint.within(boundary)) {
                return false
            }
            if (!allPoints.contains(sPoint)) {
                return moveSand(sPoint)
            }

            // TEST SW
            val swPoint = p + Direction.SOUTH + Direction.WEST
            if (!swPoint.within(boundary)) {
                return false
            }
            if (!allPoints.contains(swPoint)) {
                return moveSand(swPoint)
            }

            // TEST SE
            val sePoint = p + Direction.SOUTH + Direction.EAST
            if (!sePoint.within(boundary)) {
                return false
            }
            if (!allPoints.contains(sePoint)) {
                return moveSand(sePoint)
            }

            // we can't move S, SW, or SE, and our point is within our bounds, so it must be at rest
            this.allPoints += p
            return true
        }

        fun grid(wallChar: Char = '#', sandChar: Char = 'o', empty: Char = '.'): List<String> {
            val lines = mutableListOf<String>()
            (boundary.first.y .. boundary.second.y).forEach { y ->
                var currentLine = ""
                (boundary.first.x .. boundary.second.x).forEach { x ->
                    val p = Point(x, y)
                    currentLine += when {
                        wall.contains(p) -> wallChar
                        allPoints.contains(p) -> sandChar
                        else -> empty
                    }
                }
                lines += currentLine
            }
            return lines.toList()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}