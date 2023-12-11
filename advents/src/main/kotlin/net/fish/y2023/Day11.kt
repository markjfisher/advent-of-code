package net.fish.y2023

import net.fish.Day
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.maths.combinations
import net.fish.resourceLines

object Day11 : Day {
    private val data by lazy { parsePuzzle(resourceLines(2023, 11)) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(universe: ExpandingUniverseGraph, factor: Long = 2L): Long {
        return universe.shortestPathLength(factor)
    }
    fun doPart2(universe: ExpandingUniverseGraph, factor: Long = 1_000_000L): Long {
        return universe.shortestPathLength(factor)

    }

    data class ExpandingUniverseGraph(
        val width: Int,
        val height: Int,
        val points: Set<Point>
    ) {
        lateinit var emptyColumns: Set<Int>
        lateinit var emptyRows: Set<Int>
        private val allPairs: Sequence<Pair<Point, Point>> = points.combinations(2).map { it.zipWithNext() }.flatten()
        init {
            findEmpty()
        }

        private fun findEmpty() {
            emptyColumns = (0 until width).toSet().subtract(points.map { it.x }.toSet())
            emptyRows = (0 until height).toSet().subtract(points.map { it.y }.toSet())
        }

        fun shortestPathLength(factor: Long): Long {
            return allPairs.fold(0L) { ac, pair ->
                val bounds = listOf(pair.first, pair.second).bounds()
                val md = pair.first.manhattenDistance(pair.second)
                // Subtract 1 for the fact the MD already contains it once
                val emptyXs = emptyColumns.count { it > bounds.first.x && it < bounds.second.x } * (factor - 1L)
                val emptyYs = emptyRows.count { it > bounds.first.y && it < bounds.second.y } * (factor - 1L)
                ac + md + emptyXs + emptyYs
            }
        }
    }

    fun parsePuzzle(data: List<String>): ExpandingUniverseGraph {
        val points = mutableSetOf<Point>()
        data.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                if (c == '#') points += Point(x, y)
            }
        }
        return ExpandingUniverseGraph(data[0].length, data.size, points)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}