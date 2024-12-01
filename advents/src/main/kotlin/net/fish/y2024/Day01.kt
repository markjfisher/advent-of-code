package net.fish.y2024

import kotlin.math.abs
import net.fish.Day
import net.fish.resourceLines

object Day01 : Day {
    private val data by lazy {
        linesToLists(resourceLines(2024, 1))
    }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: Pair<List<Int>, List<Int>>): Int = (data.first zip data.second).sumOf { (a, b) -> abs(a - b) }

    fun doPart2(data: Pair<List<Int>, List<Int>>): Int {
        val frequencies = data.second.groupingBy { it }.eachCount()
        return data.first.sumOf { it * frequencies.getOrDefault(it, 0) }
    }

    private fun splitLine(line: String): Pair<Int, Int> = line.split("   ", limit = 2).map(String::toInt).let { (x, y) -> Pair(x, y) }

    fun linesToLists(lines: List<String>): Pair<List<Int>, List<Int>> = lines
        .map { splitLine(it) }
        .fold(Pair(emptyList<Int>(), emptyList<Int>())) { acc, current ->
            Pair(acc.first + current.first, acc.second + current.second)
        }.let { (l1, l2) -> Pair(l1.sorted(), l2.sorted()) }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }
}