package net.fish.y2022

import net.fish.Day
import net.fish.resourceLines

object Day17 : Day {
    private val data = resourceLines(2022, 17)

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int = data.size
    fun doPart2(data: List<String>): Int = data.size

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}