package net.fish.y2021

import net.fish.Day
import net.fish.maths.CircularArray
import net.fish.resourceString

object Day06 : Day {
    private val data = resourceString(2021, 6).split(",").map { it.toInt() }

    override fun part1() = doIterations(data, 80)
    override fun part2() = doIterations(data, 256)

    fun doIterations(initialTimes: List<Int>, iterations: Int): Long {
        // initialise the 8 counts
        val fishTimes = CircularArray(Array(9) { 0L }.toList())
        initialTimes.forEach { t ->
            fishTimes[t] = fishTimes[t] + 1
        }
        // move all counts down 1, the old 0s become 6s but spawn that many 8s
        (0 until iterations).forEach {
            val zero = fishTimes[0]
            fishTimes.rotateLeft()
            fishTimes[6] += zero
        }

        return fishTimes.sum()
    }


    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}