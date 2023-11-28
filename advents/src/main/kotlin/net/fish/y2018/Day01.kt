package net.fish.y2018

import net.fish.Day
import net.fish.resourceLines

object Day01 : Day {
    private val data by lazy { resourceLines(2018, 1).map { it.toInt() } }
    private val previousTotals: MutableSet<Int> = mutableSetOf()

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<Int>): Int = data.sum()
    fun doPart2(data: List<Int>): Int {
        var currentIndex = 0
        var foundDuplicate = false
        var currentTotal = 0
        previousTotals += 0
        // find when the total repeats itself by checking when we've seen it previously in the set, or adding to it and repeating
        while (!foundDuplicate) {
            currentTotal += data[currentIndex]
            if (previousTotals.contains(currentTotal)) {
                foundDuplicate = true
            } else {
                previousTotals += currentTotal
                currentIndex = (currentIndex + 1) % data.size
            }
        }
        return currentTotal
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}