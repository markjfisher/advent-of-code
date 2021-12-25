package net.fish.y2021

import net.fish.Day
import net.fish.resourceLines
import net.fish.seacucumber.SeaCucumberSimulator

object Day25 : Day {
    private val simulator by lazy { SeaCucumberSimulator(resourceLines(2021, 25)) }

    override fun part1() = doPart1(simulator)
    override fun part2() = doPart2(simulator)

    fun doPart1(simulator: SeaCucumberSimulator): Int = simulator.findBlockStep()
    fun doPart2(simulator: SeaCucumberSimulator): Int = 0

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}