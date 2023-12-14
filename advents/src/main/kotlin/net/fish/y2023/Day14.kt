package net.fish.y2023

import net.fish.Day
import net.fish.geometry.*
import net.fish.geometry.Direction.*
import net.fish.resourceLines
import net.fish.y2021.GridDataUtils

object Day14 : Day {
    override val warmUps: Int = 0
    private val data by lazy { resourceLines(2023, 14) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    val previousPanels = mutableListOf<Panel>()

    data class Panel(val points: Map<Point, Char>) {
        val bounds = points.map { it.key }.bounds()
        val width = bounds.second.x + 1
        val height = bounds.second.y + 1
        val columns = bounds.columns()
        val rows = bounds.rows()

        fun spin(count: Long): Panel {
            val pairOfStartLength: Pair<Int, Int> = findPanelCycle()
            val prevIndex = pairOfStartLength.first + (count - pairOfStartLength.first) % pairOfStartLength.second
            return previousPanels[prevIndex.toInt()]
        }

        private fun findPanelCycle(): Pair<Int, Int> {
            tailrec fun iter(panel: Panel): Pair<Int, Int> {
                val start = System.nanoTime()
                var newPanel = panel.move(NORTH)
                newPanel = newPanel.move(WEST)
                newPanel = newPanel.move(SOUTH)
                newPanel = newPanel.move(EAST)
                // println("spin = ${(System.nanoTime() - start) / 1_000_000}ms")
                if (previousPanels.contains(newPanel)) {
                    val firstTime = previousPanels.indexOf(newPanel)
                    val cycleTime = previousPanels.size - firstTime
//                    println("cycle found, start: $firstTime, cycle: $cycleTime")
                    return Pair(firstTime, cycleTime)
                } else {
                    previousPanels.add(newPanel)
                    return iter(newPanel)
                }
            }
            previousPanels.clear()
            previousPanels.add(this)
            return iter(this)
        }

        fun move(direction: Direction): Panel = when (direction) {
            NORTH -> {
                var hasMoved: Boolean
                val newPoints = points.toMutableMap()
                do {
                    hasMoved = false
                    rows.drop(1).forEach { row ->
                        row.filter { newPoints[it] == 'O'  && newPoints[it + direction] == '.' }.forEach { p ->
                            newPoints[p + direction] = 'O'
                            newPoints[p] = '.'
                            hasMoved = true
                        }
                    }
                } while (hasMoved)
                Panel(newPoints)
            }
            EAST -> {
                var hasMoved: Boolean
                val newPoints = points.toMutableMap()
                do {
                    hasMoved = false
                    columns.take(width - 1).toList().reversed().forEach { column ->
                        column.filter { newPoints[it] == 'O'  && newPoints[it + direction] == '.'}.forEach { p ->
                            newPoints[p + direction] = 'O'
                            newPoints[p] = '.'
                            hasMoved = true
                        }
                    }
                } while (hasMoved)
                Panel(newPoints)
            }
            SOUTH -> {
                var hasMoved: Boolean
                val newPoints = points.toMutableMap()
                do {
                    hasMoved = false
                    rows.take(height - 1).toList().reversed().forEach { row ->
                        row.filter { newPoints[it] == 'O'  && newPoints[it + direction] == '.'}.forEach { p ->
                            newPoints[p + direction] = 'O'
                            newPoints[p] = '.'
                            hasMoved = true
                        }
                    }
                } while (hasMoved)
                Panel(newPoints)
            }
            WEST -> {
                var hasMoved: Boolean
                val newPoints = points.toMutableMap()
                do {
                    hasMoved = false
                    columns.drop(1).forEach { column ->
                        column.filter { newPoints[it] == 'O' && newPoints[it + direction] == '.'}.forEach { p ->
                            newPoints[p + direction] = 'O'
                            newPoints[p] = '.'
                            hasMoved = true
                        }
                    }
                } while (hasMoved)
                Panel(newPoints)
            }
        }

        fun weight(): Long {
            return rows.foldIndexed(0L) { i, ac, row ->
                ac + (height - i) * row.count { points[it] == 'O' }
            }
        }

        override fun toString(): String {
            var s = ""
            val bounds = points.map { it.key }.bounds()
            for (y in bounds.first.y .. bounds.second.y) {
                for (x in bounds.first.x .. bounds.second.x) {
                    s += points[Point(x, y)]
                }
                s += "\n"
            }
            return s.dropLast(1)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Panel

            return points.filterValues { it == 'O' }.all { p -> other.points[p.key] == p.value }
        }

        override fun hashCode(): Int {
            return points.hashCode()
        }
    }

    fun createPanel(data: List<String>): Panel {
        return Panel(GridDataUtils.mapCharPointsFromLines(data))
    }

    fun doPart1(data: List<String>): Long {
        val panel = createPanel(data)
        return panel.move(NORTH).weight()
    }
    fun doPart2(data: List<String>): Long {
        return createPanel(data).spin(1000000000L).weight()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}