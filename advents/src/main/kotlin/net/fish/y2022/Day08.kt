package net.fish.y2022

import net.fish.Day
import net.fish.collections.takeWhileInclusive
import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.maths.IntCombinations
import net.fish.maths.Ring
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

    class PairCombinations(size: Int): IntCombinations(2) {
        override val state = Array(2) { Ring(size) }.toList()
        override fun state() = state.map { it.state() }.reversed()
    }

    data class TreeGrid(private val gridData: Map<Point, Int>) {
        private val compassDirs = listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
        private val boundary: Pair<Point, Point> = gridData.keys.bounds()
        private val treeColumns = PairCombinations(boundary.second.x + 1).groupBy { it[0] }.values.map { it.map { x -> Point(x[0], x[1]) } }
        private val treeRows = PairCombinations(boundary.second.y + 1).groupBy { it[1] }.values.map { it.map { y -> Point(y[0], y[1]) } }

        private fun allPoints(): Set<Point> = gridData.keys
        private fun onBoundary(p: Point): Boolean = p.x == boundary.first.x || p.x == boundary.second.x || p.y == boundary.first.y || p.y == boundary.second.y
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

        fun maxScenicScore(): Int = allPoints().maxOfOrNull { scenicScore(it) } ?: 0
        fun scenicScore(p: Point): Int = compassDirs.map { viewingDistance(p, it) }.reduce(Int::times)

        fun viewingDistance(p: Point, d: Direction): Int {
            if (onBoundary(p)) return 0
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
