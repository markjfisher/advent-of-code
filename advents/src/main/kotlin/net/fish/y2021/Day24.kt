package net.fish.y2021

import net.fish.Day
import net.fish.resourceLines
import kotlin.math.abs
import kotlin.math.pow

object Day24 : Day {
    private val data = resourceLines(2021, 24)

    override fun part1() = doPart1(data)
    override fun part2() = doPart2()

    fun doPart1(data: List<String>): Pair<Long, Long> = solve(data)
    fun doPart2() = 0L

    // Pencil and paper originally - until did the solution version.
    //    fun doPart1(data: List<String>): Long = 53999995829399L
    //    fun doPart2(data: List<String>): Long = 11721151118175L

    fun solve(data: List<String>): Pair<Long, Long> {
        val stack = ArrayDeque<Pair<Int, Int>>()
        var upperValue = 99999999999999L
        var lowerValue = 11111111111111L

        for (i in 0..13) {
            val a = data[i * 18 + 5].split(" ").last().toInt()
            val b = data[i * 18 + 15].split(" ").last().toInt()

            if (a > 0) {
                stack.add(Pair(i, b))
                continue
            }
            val (j, bStack) = stack.removeLast()

            val power10Upper = 13 - if (a > -bStack) j else i
            val power10Lower = 13 - if (a < -bStack) j else i
            upperValue -= abs((a + bStack) * 10.0.pow(power10Upper)).toLong()
            lowerValue += abs((a + bStack) * 10.0.pow(power10Lower)).toLong()
        }
        return Pair(upperValue, lowerValue)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}