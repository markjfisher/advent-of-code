package net.fish.y2023

import net.fish.Day
import net.fish.geometry.Direction
import net.fish.geometry.Direction.Companion.from
import net.fish.geometry.Direction.EAST
import net.fish.geometry.Direction.NORTH
import net.fish.geometry.Direction.SOUTH
import net.fish.geometry.Direction.WEST
import net.fish.geometry.Point
import net.fish.resourceLines

object Day18 : Day {
    private val instructionExtractor by lazy { Regex("""(.) ([0-9]+) \(#([a-f0-9]+)\)""") }
    private val data by lazy { resourceLines(2023, 18) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Long {
        val digSite = toDigSite(data)
        return digSite.coveredSquareCount(true)
    }
    fun doPart2(data: List<String>): Long {
        val digSite = toDigSite(data)
        return digSite.coveredSquareCount(false)
    }

    data class DigInstruction(val direction: Direction, val length: Long, val hex: String)
    data class DigSite(val instructions: List<DigInstruction>) {

        fun hexToLen(hex: String): Long = hex.substring(0, 5).toLong(radix = 16)
        fun hexToDir(hex: String): Direction = when (hex.last()) {
            '0' -> EAST
            '1' -> SOUTH
            '2' -> WEST
            '3' -> NORTH
            else -> throw Exception("Unknown direction instruction: ${hex.last()}")
        }

        fun perimeterLength(isPart1: Boolean = true): Long {
            return instructions.fold(0L) { total, instruction ->
                val length = if (isPart1) instruction.length else hexToLen(instruction.hex)
                total + length
            }
        }

        fun makeVertices(isPart1: Boolean = true): List<Point> {
            return instructions.fold(listOf(Point(0, 0))) { ac, instruction ->
                val dir = if (isPart1) instruction.direction else hexToDir(instruction.hex)
                val length = if (isPart1) instruction.length else hexToLen(instruction.hex)
                // add last point in given direction
                val newPoint = ac.last() + dir.toPoint() * length
                ac + newPoint
            }
        }

        fun shoelace(vertices: List<Point>): Long {
            return vertices.zipWithNext { p1, p2 ->
                p1.x.toLong() * p2.y - p1.y.toLong() * p2.x
            }.sum() / 2L
        }

        // picks theorem, rearranged to give i+b
        fun puzzleSquaresCount(area: Long, perimeter: Long): Long = area + perimeter/2L + 1L

        fun coveredSquareCount(isPart1: Boolean = true): Long {
            val vertices = makeVertices(isPart1)
            val perimeter = perimeterLength(isPart1)
            return puzzleSquaresCount(shoelace(vertices), perimeter)
        }

    }

    fun toDigSite(data: List<String>): DigSite {
        return DigSite(data.map { line ->
            instructionExtractor.find(line)?.destructured!!.let { (d, l, c) ->
                DigInstruction(from(d), l.toLong(), c)
            }
        })
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}