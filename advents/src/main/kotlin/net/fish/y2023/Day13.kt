package net.fish.y2023

import net.fish.Day
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.geometry.columns
import net.fish.geometry.rows
import net.fish.resourceStrings
import net.fish.y2021.GridDataUtils

object Day13 : Day {
    private val data by lazy { resourceStrings(2023, 13) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val grids = toAshRockGrid(data)
        return grids.foldIndexed(0) { i, ac, g ->
            var toAdd = 0
            val rr = g.rowReflection()
            if (rr > 0) {
                toAdd = 100 * rr
            } else {
                val rc = g.columnReflection()
                if (rc == -1) println("no column or row reflected in grid $i: $g")
                toAdd = if (rc > 0) rc else 0
            }

            ac + toAdd
        }
    }
    fun doPart2(data: List<String>): Int {
        val grids = toAshRockGrid(data)
        return grids.fold(0) { ac, g ->
            val (newRow, newColumn) = g.smudge()
            if (newRow == -1 && newColumn == -1) throw Exception("grid had no smudges:\n$g")
            if (newRow > 0 && newColumn > 0) throw Exception("grid had new reflections in both r/c:\n$g")
            var toAdd = 0
            if (newRow > 0) toAdd += 100 * newRow
            if (newColumn > 0) toAdd += newColumn
            ac + toAdd
        }
    }

    // Generates Lists of pairs of columns or rows that need to be tested for reflection
    // starting at the first value plus next value, then values outside those by 1
    // checkPairs(0, 1) = [(0,1)]
    // checkPairs(2, 4) = [(2,3), (1,4)], but (0,5) is not in the max range as 5>4
    // See tests for full case

    fun checkPairs(n: Int, max: Int): List<Pair<Int, Int>> {
        val pairs = mutableListOf<Pair<Int, Int>>()
        var round = 1
        for (i in n downTo 0) {
            val other = n + round
            if (other <= max) {
                pairs.add(Pair(i, other))
            }
            round++
        }
        return pairs
    }

    data class RockGrid(val points: Map<Point, Char>) {
        fun smudge(): Pair<Int, Int> {
            val currentRowReflection = rowReflection()
            val currentColumnReflection = columnReflection()
            var newRow = -1
            var newColumn = -1
            // BRUTE FORCE AHOY!
            points.forEach { (p, c) ->
                val newPoints = points.toMutableMap()
                newPoints[p] = if (c == '.') '#' else '.'
                val newGrid = RockGrid(newPoints.toMap())
                val rrs = newGrid.rowReflections()
                val rrsNotCurrent = rrs.filterNot { it == currentRowReflection || it == -1 }
                if (rrsNotCurrent.isNotEmpty()) {
//                    println ("p: $p ROW CHANGE\ncurrent:\n${gridString()}")
//                    println ("new:\n${newGrid.gridString()}")
                    newRow = rrsNotCurrent.first()
                } else {
                    val crs = newGrid.columnReflections()
                    val crsNotCurrent = crs.filterNot { it == currentColumnReflection || it == -1 }

                    if (crsNotCurrent.isNotEmpty()) {
    //                    println ("p: $p COL CHANGE\ncurrent:\n${gridString()}")
    //                    println ("new:\n${newGrid.gridString()}")
                        newColumn = crsNotCurrent.first()
                    }
                }
            }
            return Pair(newRow, newColumn)
        }

        private fun findReflections(lines: List<String>): List<Int> {
            val common = mutableListOf<Pair<Int, Int>>()
            for (r in 0 until lines.size - 1) {
                val toCheck = checkPairs(r, lines.size - 1)
                val pairsMatch = toCheck.all { lines[it.first] == lines[it.second] }
                if (pairsMatch) common.add(Pair(r, r + 1))
            }

            if (common.isEmpty()) return listOf(-1)
            return common.map { it.second }
        }

        private fun rowReflections(): List<Int> {
            val bounds = points.map { it.key }.bounds()
            val rows = bounds.rows().map { it.map{ p -> points[p]}.joinToString("") }.toList()
            return findReflections(rows)
        }

        private fun columnReflections(): List<Int> {
            val bounds = points.map { it.key }.bounds()
            val columns = bounds.columns().map { it.map{ p -> points[p]}.joinToString("") }.toList()
            return findReflections(columns)
        }

        fun rowReflection(): Int = rowReflections().first()
        fun columnReflection(): Int = columnReflections().first()

        private fun gridString(): String {
            var s = ""
            val bounds = points.map { it.key }.bounds()
            for (y in bounds.first.y .. bounds.second.y) {
                for (x in bounds.first.x .. bounds.second.x) {
                    s += points[Point(x, y)]
                }
                s += "\n"
            }
            return s
        }

        override fun toString(): String {
            return gridString()
        }
    }

    fun toAshRockGrid(data: List<String>): List<RockGrid> {
        return data.map { block ->
            RockGrid(GridDataUtils.mapCharPointsFromLines(block.split("\n")).toMap())
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}