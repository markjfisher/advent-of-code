package net.fish.y2021

import net.fish.Day
import net.fish.resourceLines

object Day01 : Day {
    private val heights by lazy { resourceLines(2021, 1).map { it.toInt() } }

    override fun part1() = doPart1(heights)
    override fun part2() = doPart2(heights)

    fun doPart1(heights: List<Int>): Int = calcIncreasesOverWindow(heights, 1)
    fun doPart2(heights: List<Int>): Int = calcIncreasesOverWindow(heights, 3)

    private fun calcIncreasesOverWindow(heights: List<Int>, windowSize: Int): Int =
        heights.windowed(windowSize, 1)
            .map { it.sum() }
            .windowed(2, 1)
            .count { it[0] < it[1] }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}