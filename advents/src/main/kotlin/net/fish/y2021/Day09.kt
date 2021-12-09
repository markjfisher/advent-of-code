package net.fish.y2021

import net.fish.Day
import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.resourceLines

object Day09 : Day {
    private val grid = toGrid(resourceLines(2021, 9))

    fun toGrid(input: List<String>): Grid {
        val gridMap = input.foldIndexed(mutableMapOf<Point, Int>()) { row, m, line ->
            val valuesForLine = line.windowed(1, 1).map { it.toInt() }
            valuesForLine.forEachIndexed { lineIndex, gridValue ->
                m[Point(lineIndex, row)] = gridValue
            }

            m
        }
        return Grid(gridMap)
    }

    override fun part1() = doPart1(grid)
    override fun part2() = doPart2(grid)

    fun doPart1(grid: Grid): Int {
        // find the local minima of grid.
        // look at each point, and check the neighbours are all lower than it
        return grid.allPoints().fold(0) { acc, p ->
            val currentPointValue = grid.at(p) ?: throw Exception("couldn't find point $p in grid")
            val isLocalMinima = grid.neighbourValuesNSEW(p).all { currentPointValue < it }
            acc + if (isLocalMinima) currentPointValue + 1 else 0
        }

    }

    fun doPart2(grid: Grid): Long {
        val basins = grid.basins()
        return basins.sortedByDescending { it.basinPoints.size }.take(3).fold(1L) { product, basin ->
            product * basin.basinPoints.size.toLong()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

    data class Basin(val localMinimum: Point, val basinPoints: List<Point>)

    data class Grid(private val gridData: Map<Point, Int>) {
        val boundary: Pair<Point, Point> = gridData.keys.bounds()

        fun at(p: Point): Int? = gridData[p]

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
                val currentPointValue = at(p) ?: throw Exception("couldn't find point $p in grid")
                val isLocalMinima = neighbourValuesNSEW(p).all { currentPointValue < it }
                if (isLocalMinima) acc.add(p)
                acc
            }
        }

        fun basins(): List<Basin> {
            val localMinima = localMinima()
            return localMinima.map { p ->
                val basinPoints = connectPoints(mutableListOf(p), neighbourPointsNSEW(p).toMutableSet())
                Basin(p, basinPoints)
            }
        }

        fun connectPoints(connected: MutableList<Point>, pointsToCheck: MutableSet<Point>, pointsChecked: MutableSet<Point> = mutableSetOf()): List<Point> {
            if (pointsToCheck.isEmpty()) return connected
            val newP = pointsToCheck.first()
            pointsToCheck.remove(newP)
            if (at(newP) != 9 && !connected.contains(newP)) {
                connected.add(newP)
                val neighboursOfNewPoint = neighbourPointsNSEW(newP).toSet()
                val unvisitedNeighbours = neighboursOfNewPoint - pointsChecked
                pointsToCheck.addAll(unvisitedNeighbours)
            }
            pointsChecked.add(newP)
            return connectPoints(connected, pointsToCheck, pointsChecked)
        }
    }
}