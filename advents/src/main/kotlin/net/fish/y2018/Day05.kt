package net.fish.y2018

import net.fish.Day
import net.fish.resourceString

object Day05 : Day {
    private val data by lazy { resourceString(2018, 5) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: String): Int = reduce(data).length
    fun doPart2(data: String): Int {
        val uniqueLowercaseLetters = data.map { it.lowercase() }.toSet()
        return uniqueLowercaseLetters.minOf { c ->
            val newData = data.replace(c, "").replace(c.uppercase(), "")
            reduce(newData).length
        }
    }

    private fun isOpposite(a: Char, b: Char): Boolean = (a.lowercaseChar() == b.lowercaseChar()) && ((a.isUpperCase() && b.isLowerCase()) || (a.isLowerCase() && b.isUpperCase()))

    fun reduce(data: String): String {
        val cumulative: MutableList<Char> = mutableListOf()

        data.forEach { c ->
            if (cumulative.isEmpty() || !isOpposite(c, cumulative.last())) {
                cumulative += c
            } else {
                cumulative.removeLast()
            }
        }
        return cumulative.joinToString("")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}