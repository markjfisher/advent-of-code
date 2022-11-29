package net.fish.y2022

import net.fish.Day
import net.fish.resourceLines

object Day01 : Day {
    private val data = resourceLines(2022, 1)

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int = 0
    fun doPart2(data: List<String>): Int = 0

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}