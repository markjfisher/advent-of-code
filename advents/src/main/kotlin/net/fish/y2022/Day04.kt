package net.fish.y2022

import net.fish.Day
import net.fish.resourceLines

object Day04 : Day {
    private val assignmentExtractor = Regex("""(\d+)-(\d+),(\d+)-(\d+)""")
    private val data = toAssignments(resourceLines(2022, 4))

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<Assignments>): Int = data.count { it.fullyContains() }
    fun doPart2(data: List<Assignments>): Int = data.count { it.overlaps() }

    fun toAssignments(data: List<String>): List<Assignments> = data.map { line ->
        assignmentExtractor.find(line)?.destructured!!.let { (a, b, c, d) ->
            Assignments(IntRange(a.toInt(), b.toInt()), IntRange(c.toInt(), d.toInt()))
        }
    }

    data class Assignments(val r1: IntRange, val r2: IntRange) {
        fun fullyContains(): Boolean = r1.fullyContains(r2) || r2.fullyContains(r1)
        fun overlaps(): Boolean = r1.overlaps(r2)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }
}

fun IntRange.fullyContains(t: IntRange): Boolean = this.first <= t.first && this.last >= t.last

fun IntRange.overlaps(t: IntRange): Boolean {
    return (this.first in t.first..t.last) || (this.last in t.first..t.last) ||
            (t.first in this.first..this.last) || (t.last in this.first..this.last)
}