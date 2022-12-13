package net.fish.y2022

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import net.fish.Day
import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.resourceLines
import net.fish.y2021.GridDataUtils

object Day12 : Day {
    private val data by lazy { toGrid(resourceLines(2022, 12)) }
    val t = Terminal()
    var visualize = false

    fun toGrid(input: List<String>, endValue: Int = 26): HillGrid {
        var start = Point(-1, -1)
        var end = Point(-1, -1)
        val grid = HillGrid(GridDataUtils.mapCharPointsFromLines(input).map { (p, c) ->
            val translated = when (c) {
                in ('a'..'z') -> (c - 'a' + 1)
                'S' -> {
                    if (start != Point(-1, -1)) throw Exception("Found duplicate start at $p") else start = p
                    1
                }
                'E' -> {
                    if (end != Point(-1, -1)) throw Exception("Found duplicate end at $p") else end = p
                    endValue
                }
                else -> throw Exception("Unknown char: $c")
            }
            p to translated
        }.toMap(), start, end)
        if (start == Point(-1, -1) || end == Point(-1, -1)) throw Exception("Did not find one of start or end: start: $start, end: $end")
        return grid
    }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(grid: HillGrid): Int = grid.shortestPathLength(grid.start, { it == grid.end }) { from, p ->
        grid.at(p) <= grid.at(from) + 1
    }

    fun doPart2(grid: HillGrid): Int = grid.shortestPathLength(grid.end, { grid.at(it) == 1 }) { from, p ->
        grid.at(from) <= grid.at(p) + 1
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

    data class HillGrid(private val gridData: Map<Point, Int>, val start: Point, val end: Point) {
        fun at(p: Point): Int = gridData[p] ?: throw Exception("Unknown point $p")
        private val boundary: Pair<Point, Point> = gridData.keys.bounds()

        private fun compassPoints(p: Point, canMoveTo: (Point) -> Boolean = { _ -> true }): List<Point> {
            return listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST).mapNotNull {
                val newP = p + it
                if (newP.within(boundary) && canMoveTo(newP)) newP else null
            }
        }

        fun shortestPathLength(begin: Point, isTarget: (Point) -> Boolean, canMoveTo: (Point, Point) -> Boolean): Int {
            val seen = mutableSetOf<Point>()
            seen += begin
            var distance = 0
            val batch = mutableListOf<Point>()
            batch += begin
            val nextBatch = mutableSetOf<Point>()
            while (batch.isNotEmpty()) {
                if (visualize) display(batch.toSet(), seen.toSet(), distance)
                batch.forEach { from ->
                    val connected = compassPoints(from) { p -> !seen.contains(p) && canMoveTo(from, p) }
                    connected.forEach { p ->
                        if (isTarget(p)) {
                            if (visualize) display(setOf(p), seen.toSet(), distance + 1)
                            return distance + 1
                        }
                        seen += p
                        nextBatch += p
                    }
                }
                batch.clear()
                batch.addAll(nextBatch)
                nextBatch.clear()
                distance++
            }
            throw Exception("No solution found")
        }

        private fun display(highlight: Set<Point>, seen: Set<Point>, distance: Int) {
            // Could be more efficient by only printing changes, but would need to track them
            t.println()
            t.println("Distance: ${TextColors.magenta("" + distance)}")
            t.cursor.hide(showOnExit = true)
            (0..boundary.second.y).forEach { y ->
                (0..boundary.second.x).forEach { x ->
                    val p = Point(x, y)
                    val v = when (p) {
                        start -> "S"
                        end -> "E"
                        else -> Char(at(p) + 'a'.code - 1).toString()
                    }
                    if (highlight.contains(p)) {
                        t.print(TextColors.blue(v))
                    } else if (seen.contains(p)) {
                        t.print(TextColors.yellow(v))
                    } else {
                        t.print(TextColors.brightWhite(v))
                    }
                }
                t.println()
            }
            t.cursor.move {
                up(boundary.second.y + 3)
                startOfLine()
            }
            Thread.sleep(5)
        }
    }
}