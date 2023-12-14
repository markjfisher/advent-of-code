package net.fish.y2023

import net.fish.Day
import net.fish.geometry.*
import net.fish.geometry.Direction.*
import net.fish.resourceLines
import net.fish.y2021.GridDataUtils

object Day14 : Day {
    override val warmUps: Int = 2
    private val data by lazy { resourceLines(2023, 14) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    val cache = mutableListOf<Panel>()

    data class Panel(val stones: Set<Point>, val fixed: Set<Point>, val width: Int, val height: Int) {
        fun move(direction: Direction) = move(listOf(direction))

        fun move(directions: List<Direction>): Panel {
            val moved = directions.fold(stones.toSet()) { ss, direction ->
                when(direction) {
                    NORTH -> movePoints(ss.sortedWith(compareBy { it.y }), direction)
                    SOUTH -> movePoints(ss.sortedWith(compareByDescending { it.y }), direction)
                    WEST -> movePoints(ss.sortedWith(compareBy { it.x }), direction)
                    EAST -> movePoints(ss.sortedWith(compareByDescending { it.x }), direction)
                }
            }
            return Panel(moved, fixed, width, height)
        }

        private fun movePoints(ps: List<Point>, direction: Direction): Set<Point> {
            fun inBounds(p: Point) = p.x in (0 until width) && p.y in (0 until height)
            val newPoints = ps.toMutableSet()
            ps.forEach { p ->
                // keep moving the rock in the given direction until it's can't move anymore
                var newP: Point = p
                var blocked = false
                while (!blocked) {
                    val checkP = newP + direction
                    if (!inBounds(checkP) || newPoints.contains(checkP) || fixed.contains(checkP)) {
                        blocked = true
                    } else {
                        newP = checkP
                    }
                }
                if (newP != p) {
                    newPoints.remove(p)
                    newPoints.add(newP)
                }
            }
            return newPoints
        }

        fun spin(): Panel {
            return this.move(listOf(NORTH, WEST, SOUTH, EAST))
        }

        fun findPanelCycle(): Pair<Int, Int> {
            cache.clear()
            tailrec fun iter(p: Panel): Pair<Int, Int> {
                val newPanel = p.spin()
                val prevIndex = cache.indexOf(newPanel)
                if (prevIndex != -1) {
                    val cycleTime = cache.size - prevIndex
                    return Pair(prevIndex, cycleTime)
                } else {
                    cache.add(newPanel)
                    return iter(newPanel)
                }
            }
            cache.add(this)
            return iter(this)
        }

        fun spin(count: Long): Panel {
            val cycleData = findPanelCycle()
            val prevIndex = cycleData.first + (count - cycleData.first) % cycleData.second
            return cache[prevIndex.toInt()]
        }

        fun weight(): Long {
            return stones.fold(0L) { ac, p ->
                ac + (height - p.y)
            }
        }
        override fun toString(): String {
            var s = ""
            for (y in 0 until height) {
                for (x in 0 until width) {
                    s += when {
                        stones.contains(Point(x,y)) -> "O"
                        fixed.contains(Point(x,y)) -> "#"
                        else -> "."
                    }
                }
                s += "\n"
            }
            return s.dropLast(1)
        }
    }

    fun createPanel2(data: List<String>): Panel {
        val grid = GridDataUtils.mapCharPointsFromLines(data)
        return Panel(grid.filter { it.value == 'O' }.keys, grid.filter { it.value == '#' }.keys, data[0].length, data.size)
    }

    fun doPart1(data: List<String>): Long {
        val panel = createPanel2(data)
        return panel.move(NORTH).weight()
    }
    fun doPart2(data: List<String>): Long {
        return createPanel2(data).spin(1000000000L).weight()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}