package net.fish.y2021

import net.fish.Day
import net.fish.resourceString

object Day06 : Day {
    private val data = resourceString(2021, 6).split(",").map { it.toInt() }

    override fun part1() = doIterations(data, 80)
    override fun part2() = doIterations(data, 256)

    fun doIterations(initialTimes: List<Int>, iterations: Int): Long {
        // initialise the 8 counts
        val fishTimes = (Array(9) { 0L }).toMutableList()
        initialTimes.forEach { t ->
            fishTimes[t] = fishTimes[t] + 1
        }
        // move all counts down 1, the old 0s become 6s but spawn that many 8s
        (0 until iterations).forEach { i ->
            val old0 = fishTimes[0]
            fishTimes[0] = fishTimes[1]
            fishTimes[1] = fishTimes[2]
            fishTimes[2] = fishTimes[3]
            fishTimes[3] = fishTimes[4]
            fishTimes[4] = fishTimes[5]
            fishTimes[5] = fishTimes[6]
            fishTimes[6] = fishTimes[7] + old0
            fishTimes[7] = fishTimes[8]
            fishTimes[8] = old0
        }

        return fishTimes.sum()

    }


    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}