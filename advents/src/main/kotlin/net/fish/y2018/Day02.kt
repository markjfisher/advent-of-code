package net.fish.y2018

import net.fish.Day
import net.fish.maths.combinations
import net.fish.maths.permutations
import net.fish.resourceLines

object Day02 : Day {
    private val data by lazy { toBoxes(resourceLines(2018, 2)) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun hasDuplicateOfCount(code: String, count: Int): Boolean {
        val byCharCount = code.toCharArray().toList().groupBy { it }
        // do any of the groups have the right size?
        return byCharCount.any { it.value.size == count }
    }

    data class Box(val code: String) {
        val size = code.length
        val hasPairs: Boolean = hasDuplicateOfCount(code, 2)
        val hasTriples: Boolean = hasDuplicateOfCount(code, 3)

        fun numDiffs(other: Box): Int = size - code.zip(other.code).count { it.first == it.second }
        fun removeDiffs(other: Box): String {
            return code.zip(other.code).filter { it.first == it.second }.map { it.first }.joinToString("")
        }
    }

    private fun toBoxes(data: List<String>): List<Box> {
        return data.map { Box(it) }
    }

    fun doPart1(data: List<Box>): Int = data.count { it.hasPairs } * data.count { it.hasTriples }
    fun doPart2(data: List<Box>): String {
        val x = data.combinations(2)
        val pairWith1Diff = x.first { it[0].numDiffs(it[1]) == 1 }
        return pairWith1Diff[0].removeDiffs(pairWith1Diff[1])
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}