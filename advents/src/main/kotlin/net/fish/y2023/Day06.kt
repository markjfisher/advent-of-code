package net.fish.y2023

import net.fish.Day
import net.fish.resourceLines

object Day06 : Day {
    override fun part1() = doPart1(listOf(42, 68, 69, 85), listOf(284, 1005, 1122, 1341))
    override fun part2() = doPart2(listOf(42686985L), listOf(284100511221341L))

    fun doPart1(times: List<Long>, distances: List<Long>): Long = marginOfError(times, distances)
    fun doPart2(times: List<Long>, distances: List<Long>): Long = marginOfError(times, distances)

    fun marginOfError(times: List<Long>, distances: List<Long>): Long {
        return times.mapIndexed { ti, t ->
            (0..t).count { n ->
                val dist = n * (t - n)
                dist > distances[ti]
            }
        }.fold(1L) { ac, c -> ac * c }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}