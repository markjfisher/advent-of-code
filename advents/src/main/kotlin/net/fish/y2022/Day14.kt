package net.fish.y2022

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import java.io.File
import net.fish.Day
import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.resourceLines
import kotlin.math.min

object Day14 : Day {
    private val entryPoint = Point(500, 0)
    val t = Terminal()

    var visualize = false
    var displayDrop = false
    var wallChar = "#"
    var sandChar = "o"
    var emptyChar = "."
    var sleepTime = 0L

    override fun part1() = doPart1(toWallPoints(resourceLines(2022, 14)))
    override fun part2() = doPart2(toWallPoints(resourceLines(2022, 14)))

    fun doVisual(part: String, dataPath: String, shouldDisplayDrop: String, sleep: String) {
        val wallPoints = toWallPoints(File(dataPath).readLines())
        val boundary = wallPoints.bounds()
        val shifted = wallPoints.map { Point(it.x, it.y - boundary.first.y + 3) }.toSet()

        displayDrop = shouldDisplayDrop.lowercase() == "true"
        sleepTime = sleep.toLong()

        when (part) {
            "1" -> doPart1(shifted)
            "2" -> doPart2(shifted)
        }
        t.cursor.move { setPosition(0, boundary.second.y + 2) }
    }

    fun doPart1(wallPoints: Set<Point>): Int {
        val simulator = runSimulator(wallPoints)
        return simulator.allPoints.size - simulator.wall.size
    }

    fun doPart2(wallPoints: Set<Point>): Int {
        val boundary = wallPoints.bounds()
        // we need an additional set of points for the lower floor, which is twice as wide as it is high + 1
        val lowerFloorY = boundary.second.y + 2
        val minLowerFloorX = entryPoint.x - lowerFloorY
        val maxLowerFloorX = entryPoint.x + lowerFloorY
        val newWall = wallPoints + (minLowerFloorX .. maxLowerFloorX).map { Point(it, lowerFloorY) }.toSet()
        val simulator = runSimulator(newWall)
        return simulator.allPoints.size - simulator.wall.size
    }

    private fun runSimulator(wallPoints: Set<Point>): SandSimulator {
        val simulator = SandSimulator(wallPoints)
        if (visualize) {
            t.cursor.move { clearScreen() }
            simulator.displayGrid()
        }
        do {
            val inserted = simulator.step()
        } while (inserted)
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
            // if (visualize) displayGrid()
            return canContinue
        }

        // returns if a new sand item settled in grid (true) else it ran off edge (false), and adds to the sand if it did settle (mutates Simulation state)
        private fun moveSand(p: Point): Boolean {
            if (visualize && displayDrop) displayMoving(p)

            // TEST SOUTH
            val sPoint = p + Direction.SOUTH
            if (!sPoint.within(boundary)) {
                return false
            }
            if (!allPoints.contains(sPoint)) {
                if (visualize && displayDrop) displayMoving(p, true)
                return moveSand(sPoint)
            }

            // TEST SW
            val swPoint = p + Direction.SOUTH + Direction.WEST
            if (!swPoint.within(boundary)) {
                return false
            }
            if (!allPoints.contains(swPoint)) {
                if (visualize && displayDrop) displayMoving(p, true)
                return moveSand(swPoint)
            }

            // TEST SE
            val sePoint = p + Direction.SOUTH + Direction.EAST
            if (!sePoint.within(boundary)) {
                return false
            }
            if (!allPoints.contains(sePoint)) {
                if (visualize && displayDrop) displayMoving(p, true)
                return moveSand(sePoint)
            }

            // we can't move S, SW, or SE, and our point is within our bounds, so it must be at rest
            this.allPoints += p
            if (visualize) displayMoving(p)
            return true
        }

        fun grid(): List<String> {
            val lines = mutableListOf<String>()
            (boundary.first.y .. boundary.second.y).forEach { y ->
                var currentLine = ""
                (boundary.first.x .. boundary.second.x).forEach { x ->
                    val p = Point(x, y)
                    currentLine += when {
                        wall.contains(p) -> wallChar
                        allPoints.contains(p) -> sandChar
                        else -> emptyChar
                    }
                }
                lines += currentLine
            }
            return lines.toList()
        }

        fun displayGrid() {
            // show the whole grid, then move cursor back to the start
            t.cursor.hide(showOnExit = true)
            t.cursor.move {
                setPosition(0, 0)
            }
            (boundary.first.y..boundary.second.y).forEach { y ->
                (boundary.first.x..boundary.second.x).forEach { x ->
                    val p = Point(x, y)
                    when {
                        wall.contains(p) -> t.print(TextColors.brightCyan(wallChar))
                        allPoints.contains(p) -> t.print(TextColors.brightYellow(sandChar))
                        else -> t.print(TextColors.white(emptyChar))
                    }
                }
                t.println()
            }
        }

        private fun displayMoving(sand: Point, isOldPoint: Boolean = false) {
            // move to the location of the point and display just it.
            t.cursor.move {
                setPosition(sand.x - boundary.first.x, sand.y)
            }
            if (isOldPoint) {
                t.print(TextColors.white(emptyChar))
            } else {
                t.print(TextColors.brightYellow(sandChar))
                // t.print((TextColors.black on TextColors.brightGreen)(sandChar))
            }
            Thread.sleep(sleepTime)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}