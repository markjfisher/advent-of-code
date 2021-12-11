package net.fish.y2021

import net.fish.Day
import net.fish.resourceLines

object Day11 : Day {
    private val simulator = DumboOctopusSimulator(resourceLines(2021, 11))
    override fun part1() = doPart1(simulator)
    override fun part2() = doPart2(simulator)

    fun doPart1(simulator: DumboOctopusSimulator): Long = simulator.doSteps(100)
    fun doPart2(simulator: DumboOctopusSimulator): Long = simulator.findSync() + 100

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}