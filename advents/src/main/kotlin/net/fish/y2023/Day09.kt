package net.fish.y2023

import net.fish.Day
import net.fish.resourceLines

object Day09 : Day {
    private val data by lazy { resourceLines(2023, 9) }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<String>): Long = parsePuzzle(data).sumOf { predict(it, true) }
    fun doPart2(data: List<String>): Long = parsePuzzle(data).sumOf { predict(it, false) }

    private fun parsePuzzle(data: List<String>): List<List<Long>> {
        return data.map { it.split("\\s+".toRegex()).map { i -> i.trim().toLong() } }
    }

    // original versions, before combining into single function
    fun predictForward(line: List<Long>): Long {
        fun pred(current: List<Long>, next: Long): Long {
            val diffs = current.windowed(2, 1).map { it[1] - it[0] }
            val n = next + diffs.last()
            return if (diffs.all { it == 0L } || diffs.size == 1) {
                n
            } else {
                pred(diffs, n)
            }
        }
        return pred(line, line.last())
    }

    // using windows to separate the pairs in the end.
    // The works, and kept for history, but used single function in the end as it doesn't generate multiple lists.
    // the new value is x0 = y0 - x1, where x1 = y1 - x2, ...
    // thus we end up with
    // x0 = y0 - (y1 - (y2 - (y3 - ...
    // x0 = y0 - y1 + y2 - y3 + ...
    // x0 = (y0 - y1) + (y2 - y3) + ...
    // so we can window the list into pairs, and subtract 2nd from 1st. need to add a final 0 at the end if there's not an even number of values
    fun predictBackward(line: List<Long>): Long {
        fun pred(current: List<Long>, starts: List<Long>): Long {
            val diffs = current.windowed(2, 1).map { it[1] - it[0] }
            return if (diffs.all { it == 0L } || diffs.size == 1) {
                val even = if (starts.size % 2 == 0) starts else starts + 0
                even.windowed(2, 2).sumOf { it[0] - it[1] }
            } else {
                pred(diffs, starts + diffs[0])
            }
        }
        return pred(line, listOf(line.first()))
    }

    // works both at start and end of line, we just need to alternate the adding/subtracting if it's at the start.
    private fun predict(line: List<Long>, addAtEnd: Boolean): Long {
        // recursive function that generates smaller lists (hence it ends), keeping track of the totals, which is the new value to add at front or end
        // isAdd flips between true and false in case where we are adding to front, but ignored if adding to the end
        fun pred(current: List<Long>, total: Long, isAdd: Boolean): Long {
            val diffs = current.windowed(2, 1).map { it[1] - it[0] }
            val toAdd = if (addAtEnd) diffs.last() else diffs.first()
            val addOrSubtract = if (!addAtEnd && !isAdd) -1 else 1
            val next = total + toAdd * addOrSubtract
            return if (diffs.all { it == 0L }) {
                next
            } else {
                pred(diffs, next, !isAdd)
            }
        }
        val firstValue = if (addAtEnd) line.last() else line.first()
        return pred(line, firstValue, false)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}