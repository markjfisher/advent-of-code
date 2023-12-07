package net.fish.y2023

import com.marcinmoskala.math.product
import net.fish.Day
import kotlin.math.sqrt

object Day06 : Day {
//    override fun part1() = doPart1(listOf(42, 68, 69, 85), listOf(284, 1005, 1122, 1341))
//    override fun part2() = doPart2(listOf(42686985L), listOf(284100511221341L))

    // fun doPart1(times: List<Long>, distances: List<Long>): Long = marginOfError(times, distances)
    // fun doPart2(times: List<Long>, distances: List<Long>): Long = marginOfError(times, distances)

    override fun part1() = doPart1(listOf(42, 68, 69, 85), listOf(284, 1005, 1122, 1341))
    override fun part2() = doPart2(42686985L, 284100511221341L)

    fun doPart1(times: List<Long>, distances: List<Long>): Long {
        return times.zip(distances).map { (t, d) ->
            moe2(t, d)
        }.product()
    }

    fun doPart2(time: Long, dist: Long): Long = moe2(time, dist)

    // works for part 2 equally well, in 60ms
    fun marginOfError(times: List<Long>, distances: List<Long>): Long {
        return times.mapIndexed { ti, t ->
            (0..t).count { n ->
                val dist = n * (t - n)
                dist > distances[ti]
            }
        }.product()
    }

    // but we can do better! quadratic equation is:
    // dist = speed * time
    // dist = n * (t - n)
    // n^2 - tn + dist = 0
    // we are given the dist and t values. solve for n, which are extremes
    // everything between is above dist.
    // Reduces time from 60ms to 0.00 :D
    fun moe2(time: Long, dist: Long): Long {
        val sqrt = sqrt(time * time.toDouble() - 4.0 * dist)
        var x1 = (time - sqrt) / 2.0
        val x2 = (time + sqrt) / 2.0
        // need to use x1+1 if x1 is exactly an integer
        if ((x1 - x1.toLong()) < 0.000000000001) x1 += 1.0
        return x2.toLong() - x1.toLong()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}