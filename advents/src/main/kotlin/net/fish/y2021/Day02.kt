package net.fish.y2021

import net.fish.Day
import net.fish.resourceLines

object Day02 : Day {
    private val instructionExtractor by lazy { Regex("""(forward|up|down) (\d+)""") }
    private val data by lazy { resourceLines(2021, 2) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        var depth = 0
        var forward = 0

        data.forEach { d ->
            instructionExtractor.find(d)?.destructured!!.let { (i, v) ->
                when (i) {
                    "forward" -> forward += v.toInt()
                    "down" -> depth += v.toInt()
                    "up" -> depth -= v.toInt()
                    else -> throw Exception("Unknown line: $d")
                }
            }
        }

        return depth * forward
    }

    fun doPart2(data: List<String>): Int {
        var depth = 0
        var forward = 0
        var aim = 0

        data.forEach { d ->
            instructionExtractor.find(d)?.destructured!!.let { (i, v) ->
                when (i) {
                    "forward" -> {
                        forward += v.toInt()
                        depth += aim * v.toInt()
                    }
                    "down" -> aim += v.toInt()
                    "up" -> aim -= v.toInt()
                    else -> throw Exception("Unknown line: $d")
                }
            }
        }

        return depth * forward
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}