package net.fish.y2021

import net.fish.Day
import net.fish.maths.Permutations
import net.fish.resourceLines

object Day18 : Day {
    private val data = resourceLines(2021, 18)

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Long {
        val result = SnailFishProcessor.process(data)
        return result.magnitude()
    }

    fun doPart2(data: List<String>): Long {
        var largestSum = Long.MIN_VALUE
        for(i in 0 until data.size - 1) {
            for (j in i+1 .. data.size - 1) {
                val l1 = data[i]
                val l2 = data[j]
                val s1 = SnailFishProcessor.convertToSnailFish(l1)
                val s2 = SnailFishProcessor.convertToSnailFish(l2)
                val mag1 = SnailFishProcessor.add(s1, s2).magnitude()
                if (mag1 > largestSum) largestSum = mag1
                val mag2 = SnailFishProcessor.add(s2, s1).magnitude()
                if (mag2 > largestSum) largestSum = mag2
            }
        }
        return largestSum
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}