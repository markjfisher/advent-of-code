package net.fish.y2022

import net.fish.Day
import net.fish.geometry.Direction
import net.fish.geometry.Point
import net.fish.geometry.bounds
import net.fish.resourceLines
import kotlin.math.abs

object Day09 : Day {
    private val data by lazy { toMovement(resourceLines(2022, 9)) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<RopeMove>): Int {
        return processMoves(moves = data, knotCount = 2).last().count()
    }
    fun doPart2(data: List<RopeMove>): Int {
        return processMoves(moves = data, knotCount = 10).last().count()
    }

    data class RopeMove(val dir: Direction, val steps: Int)

    fun toMovement(data: List<String>): List<RopeMove> {
        return data.map { line ->
            val d = Direction.from(line[0])
            val steps = line.split(" ", limit = 2)[1].toInt()
            RopeMove(d, steps)
        }
    }

    fun processMoves(moves: List<RopeMove>, knotCount: Int, printDebug: Boolean = false, finalOnly: Boolean = false): List<MutableSet<Point>> {
        val knotCurrentPositions = Array(knotCount) { Point(0, 0) }.toMutableList()
        val knotPositions = Array(knotCount) { mutableSetOf<Point>() }.toList()
        knotPositions.forEach { it.add(Point(0, 0)) }

        if (printDebug && !finalOnly) showGrid(knotCurrentPositions, knotPositions)
        moves.map { move ->
            if (printDebug && !finalOnly) println("Move: $move")
            repeat((1 .. move.steps).count()) {
                doMove(move.dir, knotCurrentPositions, knotPositions)
                if (printDebug && !finalOnly) showGrid(knotCurrentPositions, knotPositions)
            }
        }
        if (printDebug && finalOnly) showGrid(knotCurrentPositions, knotPositions)
        return knotPositions
    }

    private fun doMove(dir: Direction, knotCurrentPositions: MutableList<Point>, knotPositions: List<MutableSet<Point>>) {
        // process the head knot
        knotCurrentPositions[0] = knotCurrentPositions[0] + dir
        knotPositions[0].add(knotCurrentPositions[0])

        // ripple down to all the sub-knots
        knotCurrentPositions.indices.drop(1).forEach { i ->
            knotCurrentPositions[i] = processMove(knotCurrentPositions[i - 1], knotCurrentPositions[i], knotPositions[i])
        }
    }

    private fun processMove(
        head: Point,
        tail: Point,
        tailPositions: MutableSet<Point>
    ): Point {
        var knot = tail

        val dx = head.x - knot.x
        val dy = head.y - knot.y
        if (abs(dx) > 1 || abs(dy) > 1) {
            // move for tail
            knot += when {
                dx == 2 && dy == 0 -> Point(1, 0)
                dx == 2 && dy == 1 -> Point(1, 1)
                dx == 2 && dy == -1 -> Point(1, -1)
                dx == 0 && dy == 2 -> Point(0, 1)
                dx == 1 && dy == 2 -> Point(1, 1)
                dx == -1 && dy == 2 -> Point(-1, 1)
                dx == -2 && dy == 0 -> Point(-1, 0)
                dx == -2 && dy == 1 -> Point(-1, 1)
                dx == -2 && dy == -1 -> Point(-1, -1)
                dx == 0 && dy == -2 -> Point(0, -1)
                dx == 1 && dy == -2 -> Point(1, -1)
                dx == -1 && dy == -2 -> Point(-1, -1)
                // now we can move more because part 2 can make parent jump diagonally, making distance larger
                dx == 2 && dy == 2 -> Point(1,1)
                dx == 2 && dy == -2 -> Point(1, -1)
                dx == -2 && dy == 2 -> Point(-1, 1)
                dx == -2 && dy == -2 -> Point(-1, -1)
                else -> throw Exception("Couldn't process change for dx: $dx, dy: $dy")
            }
        }
        tailPositions.add(knot)
        return knot
    }

    private fun showGrid(ps: List<Point>, all: List<Set<Point>>) {
        val combined = all.fold(mutableSetOf<Point>()) { ac, s -> ac.addAll(s); ac}
        val bounds = combined.bounds()
        for (y in bounds.first.y ..  bounds.second.y) {
            for (x in bounds.first.x .. bounds.second.x) {
                val p = Point(x, y)
                val d = if (ps.contains(p)) {
                    when (p) {
                        ps[0] -> "H"
                        ps[1] -> "1"
                        ps[2] -> "2"
                        ps[3] -> "3"
                        ps[4] -> "4"
                        ps[5] -> "5"
                        ps[6] -> "6"
                        ps[7] -> "7"
                        ps[8] -> "8"
                        ps[9] -> "9"
                        else -> throw Exception("!")
                    }
                } else if (all.last().contains(p)) {
                    if (p == Point(0, 0)) "s" else "#"
                } else {
                    "."
                }
                print(d)
            }
            println("")
        }
        println("")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}