package net.fish.y2022

import net.fish.Day
import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.resourceLines

object Day24 : Day {
    private val data by lazy { resourceLines(2022, 24) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val weatherGrid = toWeatherGrid(data)
        return traverse(weatherGrid)
    }
    fun doPart2(data: List<String>): Int {
        val weatherGrid = toWeatherGrid(data)
        return traverse(weatherGrid, 2)
    }

    fun toWeatherGrid(data: List<String>): WeatherGrid {
        val asChars = data.map { it.toCharArray().toList() }
        val allPoints = asChars.indices.flatMap { y ->
            asChars[y].indices.map { x ->
                Point(x, y)
            }
        }
        val walls = allPoints.filter { (x, y) -> asChars[y][x] == '#' }.toSet()
        val blizzards = allPoints.filter { (x, y) -> asChars[y][x] != '#' && asChars[y][x] != '.' }.associateWith { p ->
            listOf(
                when (asChars[p.y][p.x]) {
                    '>' -> Direction.EAST
                    '<' -> Direction.WEST
                    'v' -> Direction.SOUTH
                    '^' -> Direction.NORTH
                    else -> throw Exception("Unknown instruction at $p: ${asChars[p.y][p.x]}")
                }
            )
        }
        val width = data[0].length
        val height = data.size
        val start = Point(1, 0)
        val end = Point(width - 2, height - 1)
        return WeatherGrid(walls, blizzards, start, end, width, height)
    }

    data class WeatherGridState(val p: Point, val t: Int, val goalsReached: List<Boolean> = listOf())

    fun moveBlizzard(state: Map<Point, List<Direction>>, width: Int, height: Int): Map<Point, List<Direction>> {
        return state.entries.fold(mutableMapOf()) { m, (p, dirs) ->
            for (d in dirs) {
                var newPos = p + d
                if (newPos.x == 0) newPos = Point(width - 2, newPos.y)
                if (newPos.x == width - 1) newPos = Point(1, newPos.y)
                if (newPos.y == 0) newPos = Point(newPos.x, height - 2)
                if (newPos.y == height - 1) newPos = Point(newPos.x, 1)
                val newDirs = m[newPos]?.toMutableList() ?: mutableListOf()
                newDirs += d
                m[newPos] = newDirs.toList()
            }
            m
        }
    }

    private val blizzardStates = mutableMapOf<Int, Map<Point, List<Direction>>>()
    private fun blizzardStateAt(t: Int, w: Int, h: Int): Map<Point, List<Direction>> {
        if (!blizzardStates.containsKey(t)) {
            blizzardStates[t] = moveBlizzard(blizzardStates[t - 1]!!, w, h)
        }
        return blizzardStates[t]!!
    }

    private val newLocations = listOf(Point(1, 0), Point(-1, 0), Point(0, 1), Point(0, -1), Point(0, 0))
    fun traverse(grid: WeatherGrid, part: Int = 1): Int {
        val initialState = WeatherGridState(grid.start, 0)
        val queue = ArrayDeque<WeatherGridState>()
        queue.addLast(initialState)
        val seen = mutableSetOf<WeatherGridState>()
        blizzardStates[0] = grid.blizzard

        while (queue.isNotEmpty()) {
            val state = queue.removeFirst()
            val seenState = state.copy(goalsReached = mutableListOf())
            if (seen.contains(seenState)) continue
            seen.add(seenState)
            val (p, t, goals) = state

            // fast exit part 2
            if (p == grid.end && goals.size == 2) return t

            val blizzardState = blizzardStateAt(t, grid.width, grid.height)

            if (!blizzardState.containsKey(p) && !grid.walls.contains(p) && p.x >= 0 && p.y >= 0 && p.x < grid.width && p.y < grid.height) {
                // we're in a non-windy spot
                newLocations.forEach { n ->
                    val newLocation = p + n
                    if (newLocation == grid.end) {
                        if (part == 1) return t + 1
                        if (goals.isEmpty()) {
                            // we made it to end first time, start again heading to start
                            queue.clear()
                            val newGoals = goals + true
                            queue.addLast(WeatherGridState(newLocation, t + 1, newGoals))
                        } else {
                            // we're at the end, on our way back, but can't move yet
                            queue.addLast(WeatherGridState(newLocation, t + 1, goals))
                        }
                    } else if (newLocation == grid.start) {
                        if (goals.size == 1) {
                            // back at the start, pick up those snacks and head to end again!
                            queue.clear()
                            val newGoals = goals + true
                            queue.addLast(WeatherGridState(newLocation, t + 1, newGoals))
                        } else {
                            queue.addLast(WeatherGridState(newLocation, t + 1, goals))
                        }
                    } else {
                        queue.addLast(WeatherGridState(newLocation, t + 1, goals))
                    }
                }
            }
        }

        throw Exception("No solution found")
    }

    fun displayBlizzard(blizzard: Map<Point, List<Direction>>, width: Int, height: Int): List<String> {
        val lines = mutableListOf<String>()
        for (j in 1 until height - 1) {
            var row = ""
            for (i in 1 until width - 1) {
                val p = Point(i, j)
                row += if (blizzard.containsKey(p)) {
                    val dirs = blizzard[p]!!
                    if (dirs.size == 1) when (dirs[0]) {
                        Direction.NORTH -> "^"
                        Direction.SOUTH -> "v"
                        Direction.EAST -> ">"
                        Direction.WEST -> "<"
                    } else dirs.size.toString()
                } else "."
            }
            lines += row
        }
        return lines.toList()
    }

    data class WeatherGrid(val walls: Set<Point>, val blizzard: Map<Point, List<Direction>>, val start: Point, val end: Point, val width: Int, val height: Int)

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}