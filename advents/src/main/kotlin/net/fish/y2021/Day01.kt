package net.fish.y2021

import net.fish.Day
import net.fish.resourceLines

object Day01 : Day {
    private val heights = resourceLines(2021, 1).map { it.toInt() }

    override fun part1() = doPart1(heights)
    override fun part2() = doPart2(heights)

    fun doPart1(heights: List<Int>): Int = calculateIncreasesOverWindow(heights, 1)
    fun doPart2(heights: List<Int>): Int = calculateIncreasesOverWindow(heights, 3)

    private fun calculateIncreasesOverWindow(heights: List<Int>, windowSize: Int): Int {
        var increases = 0
        var previousSum = 0
        val windows = heights.windowed(size = windowSize, step = 1)
        windows.forEachIndexed { i, hs ->
            if (i > 0) {
                val windowSum = hs.sum()
                if (windowSum > previousSum) increases++
            }
            previousSum = hs.sum()
        }
        return increases
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}