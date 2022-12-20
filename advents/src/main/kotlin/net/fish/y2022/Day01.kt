package net.fish.y2022

import net.fish.Day
import net.fish.resourceStrings

object Day01 : Day {
    private val data by lazy { totals(resourceStrings(2022, 1)) }

    private fun totals(data: List<String>): List<Int> {
        return data.map { elfFoodList ->
            elfFoodList.split("\n").sumOf { f -> f.toInt() }
        }
    }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(totals: List<Int>): Int {
        return totals.maxOrNull() ?: 0
    }
    fun doPart2(totals: List<Int>): Int {
        return totals.sortedDescending().subList(0, 3).sum()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}