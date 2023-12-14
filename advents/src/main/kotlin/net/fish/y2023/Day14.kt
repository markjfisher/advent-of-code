package net.fish.y2023

import net.fish.Day
import net.fish.geometry.Direction
import net.fish.geometry.Direction.EAST
import net.fish.geometry.Direction.NORTH
import net.fish.geometry.Direction.SOUTH
import net.fish.geometry.Direction.WEST
import net.fish.geometry.Point
import net.fish.resourceLines
import net.fish.y2021.GridDataUtils

object Day14 : Day {
    private val data by lazy { resourceLines(2023, 14) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    val cache = mutableListOf<Set<Point>>()

    data class Panel(val stones: MutableSet<Point>, val fixed: Set<Point>, val width: Int, val height: Int) {
        fun move(direction: Direction) = move(listOf(direction))

        fun move(directions: List<Direction>) {
            directions.forEach { direction ->
                when(direction) {
                    NORTH -> movePoints(direction) { s -> s.sortedWith(compareBy { it.y }) }
                    SOUTH -> movePoints(direction) { s -> s.sortedWith(compareByDescending { it.y }) }
                    WEST -> movePoints(direction) { s -> s.sortedWith(compareBy { it.x }) }
                    EAST -> movePoints(direction) { s -> s.sortedWith(compareByDescending { it.x }) }
                }
            }
        }

        private fun movePoints(direction: Direction, sorter: (Set<Point>) -> List<Point>) {
            fun inBounds(p: Point) = p.x in (0 until width) && p.y in (0 until height)
            sorter(stones).forEach { p ->
                // keep moving the rock in the given direction until it's can't move anymore
                var newP: Point = p
                var blocked = false
                while (!blocked) {
                    val checkP = newP + direction
                    if (!inBounds(checkP) || stones.contains(checkP) || fixed.contains(checkP)) {
                        blocked = true
                    } else {
                        newP = checkP
                    }
                }
                if (newP != p) {
                    stones.remove(p)
                    stones.add(newP)
                }
            }
        }

        fun spin() {
            this.move(listOf(NORTH, WEST, SOUTH, EAST))
        }

        fun findPanelCycle(): Pair<Int, Int> {
            cache.clear()
            // keep cycling recursively until we get a repeat, and return the start and cycle time
            tailrec fun iter(): Pair<Int, Int> {
                spin()
                val prevIndex = cache.indexOf(stones.toSet())
                if (prevIndex != -1) {
                    val cycleTime = cache.size - prevIndex
                    return Pair(prevIndex, cycleTime)
                } else {
                    cache.add(stones.toSet())
                    return iter()
                }
            }
            // start the cache with the current state of stones
            cache.add(this.stones.toSet())
            return iter()
        }

        fun spin(count: Long): Panel {
            val cycleData = findPanelCycle()
            val prevIndex = cycleData.first + (count - cycleData.first) % cycleData.second
            return Panel(cache[prevIndex.toInt()].toMutableSet(), fixed, width, height)
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

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Panel
            return stones == other.stones
        }

        override fun hashCode(): Int {
            return stones.hashCode()
        }
    }

    fun createPanel2(data: List<String>): Panel {
        val grid = GridDataUtils.mapCharPointsFromLines(data)
        return Panel(grid.filter { it.value == 'O' }.keys.toMutableSet(), grid.filter { it.value == '#' }.keys, data[0].length, data.size)
    }

    fun doPart1(data: List<String>): Long {
        val panel = createPanel2(data)
        panel.move(NORTH)
        return panel.weight()
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