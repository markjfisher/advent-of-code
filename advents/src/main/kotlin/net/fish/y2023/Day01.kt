package net.fish.y2023

import net.fish.Day
import net.fish.resourceLines
import net.fish.resourceStrings

object Day01 : Day {
    // ASSUME LIST OF INTEGERS
    private val data1 by lazy { resourceLines(2023, 1).map { it.toInt() } }

    // BLOCKS OF INTEGERS
    private val data2 by lazy { toX(resourceStrings(2023, 1)) }

    // SOME FUNCTION OVER THE BLOCK
    fun toX(blocks: List<String>) {
        blocks.map { block ->
            val blockSum = block.split("\n").sumOf { i -> i.toInt() }
        }
    }

    override fun part1() = doPart1(data1)
    override fun part2() = doPart2(data1)

    fun doPart1(data: List<Int>): Int = data.size
    fun doPart2(data: List<Int>): Int = data.size

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}