package net.fish.y2023

import net.fish.Day
import net.fish.resourceLines

object Day12 : Day {
    private val data by lazy { resourceLines(2023, 12) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        return parsePuzzle(data).sumOf { matchingOpDam(it.first, it.second) }
    }

    fun doPart2(data: List<String>): Int = 0

    fun parsePuzzle(data: List<String>): List<Pair<String, List<Int>>> = data.map { line ->
        val parts = line.split(" ", limit = 2)
        Pair(parts[0], parts[1].split(",").map { it.trim().toInt() })
    }

    fun opDamExtractor(s: String): List<String> {
        fun rec(s: String): List<String> {
            if (!s.contains('?')) return listOf(s)
            val s1 = s.replaceFirst('?', '.')
            val s2 = s.replaceFirst('?', '#')
            return if (!s1.contains('?')) listOf(s1, s2) else rec(s1) + rec(s2)
        }
        return rec(s)
    }

    fun opDamToSeq(s: String): List<Int> = s.split(".").map { it.count() }.filterNot { it == 0 }

    fun matchingOpDam(s: String, seq: List<Int>): Int {
        return opDamExtractor(s)
            .map { opDamToSeq(it) }
            .count { it == seq }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}