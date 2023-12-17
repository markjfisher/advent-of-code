package net.fish.y2023

import net.fish.Day
import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.network.GraphSearchResult
import net.fish.network.findShortestPathByPredicate
import net.fish.resourceLines
import net.fish.y2021.GridDataUtils

object Day17 : Day {
    private val data by lazy { GridDataUtils.mapIntPointsFromLines(resourceLines(2023, 17)).toMap() }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(grid: Map<Point, Int>): Int {
        val bounds = grid.keys.bounds()
        val start = PointInDir(Point(0, 0), Direction.EAST, 0)
        val end = bounds.second
        val path = findShortestPathByPredicate(
            start,
            { (p, _) -> p == end },
            { it.neighbours().filter { (n) -> n in grid }},
            { _, (p) -> grid[p]!! }
        )
        return path.getScore()
    }

    fun doPart2(grid: Map<Point, Int>): Int {
        // have to look in 2 directions, as we must move minimum of 4, so unlike p1, can't immediately turn on start position.
        return setOf(Direction.EAST, Direction.SOUTH).minOf { dir ->
            val result = searchInDirection(grid, dir)
            val score = result.getScore()
            // println("[$score]:\n${path.getPath()}\n")
            score
        }
    }

    // We track from any point the valid points in a line away from that point, so keep track of distance to next
    data class PointInDir(val point: Point, val direction: Direction, val line: Int) {
        fun neighbours(): List<PointInDir> {
            return buildList {
                // can't go more than 3 in a straight line
                if (line < 3) {
                    add(PointInDir(point + direction, direction, line + 1))
                }
                // right
                add(PointInDir(point + direction.cw(), direction.cw(), 1))
                // left
                add(PointInDir(point + direction.ccw(), direction.ccw(), 1))
            }
        }

        fun ultra(): List<PointInDir> {
            val points = buildList {
                if (line >= 4) {
                    add(PointInDir(point + direction.cw(), direction.cw(), 1))
                    add(PointInDir(point + direction.ccw(), direction.ccw(), 1))
                }
                // we have to pass through everything, and accumulate its score, so neighbours includes the points where line <4.
                // those with less will be filtered out as a target check.
                if (line < 10) {
                    add(PointInDir(point + direction, direction, line + 1))
                }
            }
            // println("ultra points for $this = $points")
            return points
        }
    }

    fun searchInDirection(
        grid: Map<Point, Int>,
        startDirection: Direction
    ): GraphSearchResult<PointInDir> {
        val bounds = grid.keys.bounds()
        val start = PointInDir(Point(0, 0), startDirection, 0)
        val end = bounds.second
        return findShortestPathByPredicate(
            start,
            { (p, _, line) ->
                // println("testing $p == $end, and $line >= 4")
                p == end && line >= 4
            },
            { it.ultra().filter { (p) -> p in grid }},
            // from doesn't matter, only interested in target position's score. we know it exists, it's been filtered
            { _, (p) -> grid[p]!! }
        )
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}