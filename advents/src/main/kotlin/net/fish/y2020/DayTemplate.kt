package net.fish.y2020

import mu.KotlinLogging
import net.fish.Day
import net.fish.resourceLines

private val logger = KotlinLogging.logger {}

object DayTemplate : Day {
    private val data = resourceLines(2020, 0)

    override fun part1() = ""
    override fun part2() = ""

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}