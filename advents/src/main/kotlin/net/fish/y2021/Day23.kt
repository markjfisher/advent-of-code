package net.fish.y2021

import net.fish.Day
import net.fish.resourceLines

object Day23 : Day {
    // private val data = resourceLines(2021, 23)

    override fun part1() = doPart1()
    override fun part2() = doPart2()

    // Solved with paper and pen today
    fun doPart1(): Int = 14148
    fun doPart2(): Int = 43814

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}