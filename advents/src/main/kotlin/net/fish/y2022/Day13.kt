package net.fish.y2022

import net.fish.Day
import net.fish.resourceStrings

object Day13 : Day {
    private val data by lazy { resourceStrings(2022, 13) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val pairs = data.map { it.split("\n") }
        val dsp = DistressSignalProcessor.parseData(pairs)
        var total = 0
        dsp.forEachIndexed { index, dspPair ->
            total += if (dspPair.left < dspPair.right) index + 1 else 0
        }
        return total
    }

    fun doPart2(data: List<String>): Int {
        val withAdditional = data + listOf("[[2]]\n[[6]]")
        val dspSorted = DistressSignalProcessor
            .parseData(withAdditional.map { it.split("\n") })
            .flatMap { listOf(it.left, it.right) }
            .sortedWith(DSPList)
        val lower = dspSorted.indexOf(DSPList(mutableListOf(DSPList(mutableListOf(DSPValue(2)))))) + 1
        val upper = dspSorted.indexOf(DSPList(mutableListOf(DSPList(mutableListOf(DSPValue(6)))))) + 1
        return lower * upper
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}
