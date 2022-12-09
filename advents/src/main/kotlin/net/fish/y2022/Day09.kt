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
        val (headPositions, tailPositions1, tailPositions9) = processMoves(data, 1)
        return tailPositions1.size
    }
    fun doPart2(data: List<RopeMove>): Int {
        val (headPositions, tailPositions1, tailPositions9) = processMoves(data, 9)
        return tailPositions9.size
    }

    fun processMoves(moves: List<RopeMove>, tailCount: Int): Triple<Set<Point>, Set<Point>, Set<Point>> {
        var head = Point(0, 0)
        var tail1 = Point(0, 0)
        var tail2 = Point(0, 0)
        var tail3 = Point(0, 0)
        var tail4 = Point(0, 0)
        var tail5 = Point(0, 0)
        var tail6 = Point(0, 0)
        var tail7 = Point(0, 0)
        var tail8 = Point(0, 0)
        var tail9 = Point(0, 0)

        val headPositions = mutableSetOf<Point>()
        val tailPositions1 = mutableSetOf<Point>()
        val tailPositions2 = mutableSetOf<Point>()
        val tailPositions3 = mutableSetOf<Point>()
        val tailPositions4 = mutableSetOf<Point>()
        val tailPositions5 = mutableSetOf<Point>()
        val tailPositions6 = mutableSetOf<Point>()
        val tailPositions7 = mutableSetOf<Point>()
        val tailPositions8 = mutableSetOf<Point>()
        val tailPositions9 = mutableSetOf<Point>()
        headPositions.add(head)
        tailPositions1.add(tail1)
        tailPositions2.add(tail2)
        tailPositions3.add(tail3)
        tailPositions4.add(tail4)
        tailPositions5.add(tail5)
        tailPositions6.add(tail6)
        tailPositions7.add(tail7)
        tailPositions8.add(tail8)
        tailPositions9.add(tail9)
        moves.map { move ->
            repeat((1 .. move.steps).count()) { currentMove ->
                // println("Before move $move ($currentMove)")
                //showGrid(listOf(head, tail1, tail2, tail3, tail4, tail5, tail6, tail7, tail8, tail9))
                //println("currentMove: $currentMove, move: $move\nbefore: h: $head, t1: $tail1, t2: $tail2, t3: $tail3, t4: $tail4, t5: $tail5, t6: $tail6, t7: $tail7, t8: $tail8, t9: $tail9")
                head += move.dir
                headPositions.add(head)
                tail1 = processMove(head, tail1, tailPositions1)
                tail2 = processMove(tail1, tail2, tailPositions2)
                tail3 = processMove(tail2, tail3, tailPositions3)
                tail4 = processMove(tail3, tail4, tailPositions4)
                tail5 = processMove(tail4, tail5, tailPositions5)
                tail6 = processMove(tail5, tail6, tailPositions6)
                tail7 = processMove(tail6, tail7, tailPositions7)
                tail8 = processMove(tail7, tail8, tailPositions8)
                tail9 = processMove(tail8, tail9, tailPositions9)
                //println(" after:")
                //showGrid(listOf(head, tail1, tail2, tail3, tail4, tail5, tail6, tail7, tail8, tail9))

            }
        }
        return Triple(headPositions, tailPositions1, tailPositions9)
    }

    private fun showGrid(ps: List<Point>) {
        val bounds = ps.bounds()
        println(bounds)
        for (y in -10 ..  10) {
            for (x in -10 .. 10) {
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
                } else {
                    "."
                }
                print(d)
            }
            println("")
        }
    }

    private fun processMove(
        head: Point,
        tail: Point,
        tailPositions: MutableSet<Point>
    ): Point {
        var tail1 = tail

        val dx = head.x - tail1.x
        val dy = head.y - tail1.y
        if (abs(dx) > 1 || abs(dy) > 1) {
            // move for tail
            tail1 += when {
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
        tailPositions.add(tail1)
        return tail1
    }

    data class RopeMove(val dir: Direction, val steps: Int)

    fun toMovement(data: List<String>): List<RopeMove> {
        return data.map { line ->
            val d = Direction.from(line[0])
            val steps = line.split(" ", limit = 2)[1].toInt()
            RopeMove(d, steps)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}