package net.fish.y2022

import net.fish.Day
import net.fish.collections.takeWhileInclusive
import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.maths.PairCombinations
import net.fish.resourceLines
import net.fish.y2021.GridDataUtils

object Day08 : Day {
    private val grid by lazy { toTreeGrid(resourceLines(2022, 8)) }

    override fun part1() = doPart1(grid)
    override fun part2() = doPart2(grid)

    fun doPart1(grid: TreeGrid): Int = grid.countVisibleTrees()
    fun doPart2(grid: TreeGrid): Int = grid.maxScenicScore()

    fun toTreeGrid(data: List<String>): TreeGrid {
        return TreeGrid(GridDataUtils.mapIntPointsFromLines(data))
    }

    data class TreeGrid(private val gridData: Map<Point, Int>) {
        private val compassDirs = listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
        private val boundary: Pair<Point, Point> = gridData.keys.bounds()
        // PairCombinations are lists of "lists of pairs". see tests.
        // This orders the combinations of (0,0), (0,1), ... into by column or row. See tests
        // list of N lists of the columns point locations, {(0,0), (0,1), (0,2), (0,3), ...}, {(1,0), (1,1), (1,2), (1,3), ...}, ...
        private val treeColumns = PairCombinations(boundary.second.x + 1).groupBy { it[0] }.values.map { it.map { p -> Point(p[0], p[1]) } }
        // list of N lists of the rows point locations, {(0,0), (1,0), (2,0), (3,0), ...}, {(0,1), (1,1), (2,1), (3,1), ...}, ...
        private val treeRows = PairCombinations(boundary.second.y + 1).groupBy { it[1] }.values.map { it.map { p -> Point(p[0], p[1]) } }

        private fun allPoints(): Set<Point> = gridData.keys
        private fun onBoundary(p: Point): Boolean =
            p.x == boundary.first.x || p.x == boundary.second.x || p.y == boundary.first.y || p.y == boundary.second.y

        fun at(p: Point): Int = gridData[p] ?: throw Exception("Unknown point $p")

        fun countVisibleTrees(): Int {
            val allVisible = mutableSetOf<Point>()
            treeRows.forEach { trees -> allVisible.addVisibleTrees(trees); allVisible.addVisibleTrees(trees.reversed()) }
            treeColumns.forEach { trees -> allVisible.addVisibleTrees(trees); allVisible.addVisibleTrees(trees.reversed()) }
            return allVisible.size
        }

        private fun MutableSet<Point>.addVisibleTrees(trees: List<Point>) {
            var maxHeight = -1
            trees.forEach { tree ->
                if (at(tree) > maxHeight) {
                    maxHeight = at(tree)
                    this.add(tree)
                }
            }
        }

        fun maxScenicScore(): Int = allPoints().filter { !onBoundary(it) }.maxOfOrNull { scenicScore(it) } ?: 0
        fun scenicScore(p: Point): Int = compassDirs.map { viewingDistance(p, it) }.reduce(Int::times)

        fun viewingDistance(p: Point, d: Direction): Int {
            return pointsToBoundaryInDir(p, d).takeWhileInclusive { at(it) < at(p) }.count()
        }

        private fun pointsToBoundaryInDir(p: Point, dir: Direction): Sequence<Point> {
            return generateSequence(p + dir) { it + dir }.takeWhileInclusive { !onBoundary(it) }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }
}
