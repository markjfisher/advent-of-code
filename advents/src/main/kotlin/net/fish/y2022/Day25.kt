package net.fish.y2022

import net.fish.Day
import net.fish.resourceLines

object Day25 : Day {
    private val data by lazy { resourceLines(2022, 25) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): String {
        return toSnafu(data.sumOf { l -> fromSnafu(l) })
    }

    fun doPart2(data: List<String>): Int = data.size - 127

    private val toDigits = mapOf("0" to 0L, "1" to 1L, "2" to 2L, "-" to -1L, "=" to -2L)
    private val fromDigits = toDigits.entries.associate { it.value to it.key }

    fun fromSnafu(s: String): Long {
        return s.fold(0L) { ac, d ->
            ac * 5L + toDigits[d.toString()]!!
        }
    }

    fun toSnafu(n: Long): String {
        return if (n == 0L) ""
        else when (val m = n % 5L) {
            0L, 1L, 2L -> toSnafu(n / 5L) + fromDigits[m]
            3L, 4L -> toSnafu(n / 5L + 1L) + fromDigits[m - 5L]
            else -> throw Exception("modulus fail for $n")
        }

    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}