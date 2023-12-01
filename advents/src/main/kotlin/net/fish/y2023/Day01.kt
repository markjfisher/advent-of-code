package net.fish.y2023

import net.fish.Day
import net.fish.resourceLines

object Day01 : Day {
    private val data by lazy { resourceLines(2023, 1) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int = data
        .map { it.toCharArray().filter { c -> c.isDigit() } } // convert to chars of just the digits
        .map { it.map { d -> d.digitToInt() } }               // convert to list of ints
        .sumOf { it.first() * 10 + it.last() }                // add of (10*first + last)

    fun doPart2(data: List<String>): Int = data
        .map { replaceNamesWithNumbers(it) }                  // convert string with named numbers to list of just digits
        .sumOf { it.first() * 10 + it.last() }                // as part 1, sum 10 * first + last

    fun replaceNamesWithNumbers(s: String): List<Int> {
        val ints = mutableListOf<Int>()
        var index = 0
        while (index < s.length) {
            val c = s.toCharArray()[index]
            when {
                c.isDigit() -> ints += "$c".toInt()
                s.substring(index).startsWith("one") -> ints += 1
                s.substring(index).startsWith("two") -> ints += 2
                s.substring(index).startsWith("three") -> ints += 3
                s.substring(index).startsWith("four") -> ints += 4
                s.substring(index).startsWith("five") -> ints += 5
                s.substring(index).startsWith("six") -> ints += 6
                s.substring(index).startsWith("seven") -> ints += 7
                s.substring(index).startsWith("eight") -> ints += 8
                s.substring(index).startsWith("nine") -> ints += 9
            }
            index++
        }
        return ints
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}