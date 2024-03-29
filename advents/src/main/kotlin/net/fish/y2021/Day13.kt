package net.fish.y2021

import net.fish.Day
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.resourceLines

object Day13 : Day {
    private val data by lazy { resourceLines(2021, 13) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int = createFoldingGrid(data).fold(1).points.size
    fun doPart2(data: List<String>): String = createFoldingGrid(data).fold().toGridString()

    fun createFoldingGrid(data: List<String>): FoldingGrid {
        val points = mutableSetOf<Point>()
        val folds = mutableListOf<Fold>()
        data.forEach { line ->
            when {
                line.startsWith("fold along x=") -> folds.add(Fold(false, line.split("=")[1].toInt()))
                line.startsWith("fold along y=") -> folds.add(Fold(true, line.split("=")[1].toInt()))
                line.isNotEmpty() -> points.add(Point(line.split(",")[0].toInt(), line.split(",")[1].toInt()))
            }
        }
        return FoldingGrid(points, folds)
    }

    data class FoldingGrid(
        val points: Set<Point>,
        val folds: List<Fold>
    ) {
        fun fold(count: Int = folds.size): FoldingGrid {
            tailrec fun performFolds(count: Int, points: Set<Point>, folds: List<Fold>): FoldingGrid {
                if (count == 0) return FoldingGrid(points, folds)
                val fold = folds.first()
                val newPoints = when {
                    fold.isHorizontal -> horizontalFold(fold.foldAlong, points)
                    else -> verticalFold(fold.foldAlong, points)
                }
                return performFolds(count - 1, newPoints, folds.drop(1))
            }

            return performFolds(count, points, folds)
        }

        private fun horizontalFold(row: Int, points: Set<Point>): Set<Point> {
            return points.fold(setOf()) { newSet, point ->
                newSet + if (point.y < row) point else Point(point.x, 2 * row - point.y)
            }
        }

        private fun verticalFold(column: Int, points: Set<Point>): Set<Point> {
            return points.fold(setOf()) { newSet, point ->
                newSet + if (point.x < column) point else Point(2 * column - point.x, point.y)
            }
        }

        fun toGridString(ps: Set<Point> = points): String {
            val bounds = ps.bounds()
            val (maxX, maxY) = bounds.second
            var output = "\n"
            (0 .. maxY).forEach { y ->
                (0 .. maxX).forEach { x ->
                    val hasPoint = ps.contains(Point(x, y))
                    output += if (hasPoint) "█" else " "
                }
                output += "\n"
            }
            return output
        }
    }

    data class Fold(
        val isHorizontal: Boolean,
        val foldAlong: Int
    )

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}