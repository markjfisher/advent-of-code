package net.fish.y2021

import net.fish.Day
import net.fish.resourceLines

object Day08 : Day {
    private val data = resourceLines(2021, 8)

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Int {
        val simpleDigitLengths = setOf(2, 3, 4, 7)
        return data.fold(0) { acc, line ->
            val rhs = line.split(" | ")[1]
            acc + rhs.split(" ").count { it.length in simpleDigitLengths }
        }
    }

    fun doPart2(data: List<String>): Int {
        val powersOf10 = listOf(1, 10, 100, 1000)
        return data.fold(0) { acc, line ->
            val (lhs, rhs) = line.split(" | ").let { (a, b) -> Pair(a.split(" ").toMutableSet(), b.split(" ")) }

            // known digits by lengths
            val d1 = findAndRemoveStringWithLength(2, lhs)
            val d4 = findAndRemoveStringWithLength(4, lhs)
            val d7 = findAndRemoveStringWithLength(3, lhs)
            val d8 = findAndRemoveStringWithLength(7, lhs)

            val d3 = findAndRemoveStringWithLengthContaining(5, d1, lhs)
            val d9 = findAndRemoveStringWithLengthContaining(6, d4, lhs)

            // just have 0[l:6], 2[l:5], 5[l:5], 6[l:6] left.
            // of len 6 [0, 6], only 0 shares commonly with 1
            val d0 = findAndRemoveStringWithLengthContaining(6, d1, lhs)
            val d6 = findAndRemoveStringWithLength(6, lhs)

            // just have 2, 5 left. 5 is wholly contained in 6
            val d5 = findAndRemoveStringWhollyContainedIn(d6, lhs)
            // Only entry left is a set of 1 string, convert it to a set of chars.
            val d2 = lhs.first().toSet()

            val setsToDigit = mapOf(
                d0 to 0, d1 to 1, d2 to 2, d3 to 3, d4 to 4, d5 to 5, d6 to 6, d7 to 7, d8 to 8, d9 to 9
            )

            // Now calculate the RHS. Guaranteed only 4 digits, so can use fixed powers of 10 to do 10^i
            val rhsValue = rhs.foldIndexed(0) { i, sum, s ->
                sum + setsToDigit.getOrDefault(s.toSet(), 0) * powersOf10[3 - i]
            }

            acc + rhsValue
        }
    }

    private fun findAndRemoveStringWithLengthContaining(length: Int, matchingAll: Set<Char>, set: MutableSet<String>): Set<Char> {
        val foundString = set.first { it.length == length && it.toSet().containsAll(matchingAll) }
        set.remove(foundString)
        return foundString.toSet()
    }

    private fun findAndRemoveStringWithLength(length: Int, set: MutableSet<String>): Set<Char> {
        val foundString = set.first { it.length == length }
        set.remove(foundString)
        return foundString.toSet()
    }

    private fun findAndRemoveStringWhollyContainedIn(superSet: Set<Char>, set: MutableSet<String>): Set<Char> {
        val foundString = set.first { (it.toSet() - superSet).isEmpty() }
        set.remove(foundString)
        return foundString.toSet()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}