package net.fish.y2021

import net.fish.Day
import net.fish.resourceLines

object Day16 : Day {
    private val data = resourceLines(2021, 16)[0]

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: String): Int {
        val bitsProcessor = BitsProcessor(data)
        return bitsProcessor.sumVersions()
    }

    fun doPart2(data: String): Long {
        val bitsProcessor = BitsProcessor(data)
        return bitsProcessor.readPacket().calc()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}