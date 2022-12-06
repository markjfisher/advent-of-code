package net.fish.y2022

import net.fish.Day
import net.fish.resourceString

object Day06 : Day {
    private val data = resourceString(2022, 6).toList()

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<Char>): Int = findMarker(data, 4)
    fun doPart2(data: List<Char>): Int = findMarker(data, 14)

    private fun findMarker(data: List<Char>, sequenceLength: Int): Int {
        val uniqueLetters = mutableListOf<Char>()
        data.forEachIndexed { i, c ->
            if (!uniqueLetters.contains(c)) {
                uniqueLetters += c
                if (uniqueLetters.size == sequenceLength) return i + 1
            } else {
                val x = uniqueLetters.dropWhile { it != c }.drop(1)
                uniqueLetters.clear()
                uniqueLetters.addAll(x)
                uniqueLetters += c
            }
        }
        return 0
    }

    @JvmStatic
    fun main(args: Array<String>) {

        println(part1())
        println(part2())
    }

}