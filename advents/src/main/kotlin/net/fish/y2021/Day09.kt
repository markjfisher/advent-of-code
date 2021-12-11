package net.fish.y2021

import net.fish.Day
import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.resourceLines

object Day09 : Day {
    private val grid = toGrid(resourceLines(2021, 9))

    fun toGrid(input: List<String>): Grid {
        return Grid(GridDataUtils.mapPointsFromLines(input))
    }

    override fun part1() = doPart1(grid)
    override fun part2() = doPart2(grid)

    fun doPart1(grid: Grid): Int {
        return grid.localMinima().fold(0) { acc, p -> acc + grid.at(p) + 1 }
    }

    fun doPart2(grid: Grid): Int {
        return grid.basins().sortedByDescending { it.basinPoints.size }.take(3).fold(1) { product, basin ->
            product * basin.basinPoints.size
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

    data class Basin(val localMinimum: Point, val basinPoints: Set<Point>)

    data class Grid(private val gridData: Map<Point, Int>) {
        val boundary: Pair<Point, Point> = gridData.keys.bounds()
        private val localMinima = localMinima()

        fun at(p: Point): Int = gridData[p] ?: throw Exception("Unknown point $p")

        fun neighbourPointsNSEW(p: Point): List<Point> {
            val neighbours = listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
            return neighbours.mapNotNull {
                val newP = p + it
                if ((newP.x >= 0 && newP.x <= boundary.second.x) && (newP.y >= 0 && newP.y <= boundary.second.y)) newP else null
            }
        }

        fun neighbourValuesNSEW(p: Point): List<Int> {
            return neighbourPointsNSEW(p).mapNotNull { gridData[it] }
        }

        fun allPoints(): Set<Point> {
            return gridData.keys
        }

        fun localMinima(): List<Point> {
            return allPoints().fold(mutableListOf()) { acc, p ->
                val currentPointValue = at(p)
                val isLocalMinima = neighbourValuesNSEW(p).all { currentPointValue < it }
                if (isLocalMinima) acc.add(p)
                acc
            }
        }

        fun basins(): List<Basin> {
            return localMinima.map { calculateBasinFor(it) }
        }

        fun calculateBasinFor(p: Point): Basin {
            val connectedPoints = connectPoints(mutableSetOf(p), neighbourPointsNSEW(p).toMutableSet())
            return Basin(localMinima.intersect(connectedPoints).first(), connectedPoints)
        }

        tailrec fun connectPoints(connected: MutableSet<Point>, pointsToCheck: MutableSet<Point>, pointsChecked: MutableSet<Point> = mutableSetOf()): Set<Point> {
            if (pointsToCheck.isEmpty()) return connected
            val currentPoint = pointsToCheck.first()
            pointsToCheck.remove(currentPoint)
            if (at(currentPoint) != 9) {
                connected.add(currentPoint)
                val neighboursOfNewPoint = neighbourPointsNSEW(currentPoint).toSet()
                val unvisitedNeighbours = neighboursOfNewPoint - pointsChecked
                pointsToCheck.addAll(unvisitedNeighbours)
            }
            pointsChecked.add(currentPoint)
            return connectPoints(connected, pointsToCheck, pointsChecked)
        }
    }
}