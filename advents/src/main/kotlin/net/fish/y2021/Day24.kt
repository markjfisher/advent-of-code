package net.fish.y2021

import net.fish.Day
import net.fish.resourceLines

object Day24 : Day {
    private val data = resourceLines(2021, 24)

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    // Pencil and paper again, matching pairs of 26/1 values
    fun doPart1(data: List<String>): Long = 53999995829399L
    fun doPart2(data: List<String>): Long = 11721151118175L

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}