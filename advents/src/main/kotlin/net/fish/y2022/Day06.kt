package net.fish.y2022

import net.fish.Day
import net.fish.resourceString

object Day06 : Day {
    private val data by lazy { resourceString(2022, 6).toList() }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<Char>): Int = findMarker(data, 4)
    fun doPart2(data: List<Char>): Int = findMarker(data, 14)

    fun findMarker(data: List<Char>, sequenceLength: Int): Int {
        data.indices.forEach { i ->
            if (data.subList(i, i + sequenceLength).toSet().size == sequenceLength) return i + sequenceLength
        }
        throw Exception("No solution found")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}