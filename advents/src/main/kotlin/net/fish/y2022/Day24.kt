package net.fish.y2022

import net.fish.Day
import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.geometry.abs
import net.fish.resourceLines
import kotlin.math.sqrt

object Day24 : Day {
    override val warmUps = 1
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

    data class WeatherGridState(val p: Point, val t: Int, val goalsReached: Int = 0)

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
        val i = t % ((w - 2) * (h - 2))
        if (!blizzardStates.containsKey(i)) {
            blizzardStates[i] = moveBlizzard(blizzardStates[i - 1]!!, w, h)
        }
        return blizzardStates[i]!!
    }

    private val newLocations = listOf(Point(1, 0), Point(-1, 0), Point(0, 1), Point(0, -1), Point(0, 0))

    fun traverse(grid: WeatherGrid, part: Int = 1): Int {
        val initialState = WeatherGridState(grid.start, 0)
        val queue = ArrayDeque<WeatherGridState>()
        queue.addLast(initialState)
        val seen = mutableSetOf<Pair<Point, Int>>()
        blizzardStates[0] = grid.blizzard

        while (queue.isNotEmpty()) {
            val state = queue.removeFirst()
            val (p, t, goals) = state

            val seenState = Pair(p, t)
            if (seen.contains(seenState)) continue
            seen.add(seenState)

            // println("checking p: $p, t: $t, goals: $goals")

            // fast exit part 2
            if (p == grid.end && goals == 2) return t

            val blizzardState = blizzardStateAt(t, grid.width, grid.height)
            if (!blizzardState.containsKey(p)) {
                // we're in a valid non-windy spot
                newLocations.forEach { n ->
                    val newLocation = p + n
                    if (newLocation == grid.end) {
                        if (part == 1) return t + 1
                        if (goals == 0) {
                            println("Changed to state 1 at time $t")
                            // we made it to end first time, start again heading to start
                            queue.clear()
                            queue.addLast(WeatherGridState(newLocation, t + 1, 1))
                        } else {
                            // we're at the end, on our way back, but can't move yet
                            queue.addLast(WeatherGridState(newLocation, t + 1, goals))
                        }
                    } else if (newLocation == grid.start && goals == 1) {
                        println("Changed to state 2 at time $t")
                        queue.clear()
                        queue.addLast(WeatherGridState(newLocation, t + 1, 2))
                    } else if (grid.contains(newLocation)) {
                        queue.addLast(WeatherGridState(newLocation, t + 1, goals))
                    }
                }
            }
        }

        throw Exception("No solution found")
    }

    // Can't get this to work - attempting to emulate https://gist.github.com/dtinth/f7675dfc0a028e1e65cbfd4b331def58 to be able to use beam search
    // But it goes wrong!
    fun traverse2(grid: WeatherGrid, part: Int = 1): Int {
        val initialState = WeatherGridState(grid.start, 0)
        val queue = ArrayDeque<WeatherGridState>()
        queue.addLast(initialState)
        val seen = mutableSetOf<Pair<Point, Int>>()
        blizzardStates[0] = grid.blizzard

        while (true) {
            val newStates = mutableListOf<WeatherGridState>()
            while (queue.isNotEmpty()) {
                val state = queue.removeFirst()
                val (p, t, goals) = state

                val seenState = Pair(p, t)
                if (seen.contains(seenState)) continue
                seen.add(seenState)

                // println("checking p: $p, t: $t, goals: $goals")

                // fast exit part 2
                if (p == grid.end && goals == 2) return t

                val blizzardState = blizzardStateAt(t, grid.width, grid.height)

                var added = 0
                newLocations.forEach { n ->
                    val newLocation = p + n
                    if (grid.contains(newLocation) && !blizzardState.containsKey(newLocation)) {
                        val newGoals = when {
                            goals == 0 && newLocation == grid.end -> {
                                queue.clear()
                                println("changed to state 1 at time $t")
                                1
                            }
                            goals == 1 && newLocation == grid.start -> {
                                queue.clear()
                                println("changed to state 2 at time $t")
                                2
                            }
                            else -> goals
                        }
                        newStates.add(WeatherGridState(newLocation, t + 1, newGoals))
                        added++
                    }
                    if (newLocation == grid.end && part == 1) {
                        return t + 1
                    }
//                    if (newLocation == grid.end && part == 2 && goals == 2) {
//                        return t + 1
//                    }
//                    if (newLocation == grid.end && )
                }
                // println("Added: $added")
            }
            // try and reduce the states by taking beam approach, taking top 50 states
            // val sorted = newStates.sortedBy { (p, t, goals) -> grid.heuristic(p, t, goals) }
            // queue.addAll(sorted)
            queue.addAll(newStates)
        }

        // throw Exception("No solution found")
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

    data class WeatherGrid(val walls: Set<Point>, val blizzard: Map<Point, List<Direction>>, val start: Point, val end: Point, val width: Int, val height: Int) {
        private val bounds = Pair(Point(1, 1), Point(width - 2, height - 2))
        private val hypot = sqrt(width.toFloat() * width + height * height).toInt()
        fun contains(p: Point): Boolean {
            return (p == start || p == end || p.within(bounds)) && !walls.contains(p)
        }

        fun heuristic(location: Point, time: Int, goals: Int): Int {
            val h = time + ((if (goals == 0) end else start) - location).abs().let { it.x + it.y } + hypot * (if (goals == 0) 2 else 1)
            // println("p: $location, t: $time, goals: $goals, h: $h")
            return h
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // println(part1())
        println(part2())
    }

}