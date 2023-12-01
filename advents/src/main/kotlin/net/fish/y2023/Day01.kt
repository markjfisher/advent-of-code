package net.fish.y2023

import net.fish.Day
import net.fish.resourceLines

object Day01 : Day {
    private val data by lazy { resourceLines(2023, 1) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int = data
        .map { parseNumbers(it, numberMap) }
        .sumOf { it.first() * 10 + it.last() }

    fun doPart2(data: List<String>): Int = data
        .map { parseNumbers(it, nameMap + numberMap) }
        .sumOf { it.first() * 10 + it.last() }

    val numberMap = mapOf(
        "1" to 1, "2" to 2, "3" to 3, "4" to 4, "5" to 5, "6" to 6, "7" to 7, "8" to 8, "9" to 9, "0" to 0
    )

    val nameMap = mapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9,
    )

    fun parseNumbers(s: String, m: Map<String, Int>): List<Int> {
        val ints = mutableListOf<Int>()
        var index = s.length
        do {
            // by looking backwards, we avoid things like "oneight" being a problem.
            val indexMatch = s.findLastAnyOf(m.keys, index, false)
            if (indexMatch != null) {
                // We're looking backwards, so always add new number to front
                ints.add(0, m[indexMatch.second]!!)
                index = indexMatch.first - 1
            }
        } while (indexMatch != null)
        return ints
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }
}