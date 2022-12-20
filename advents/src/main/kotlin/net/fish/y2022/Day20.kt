package net.fish.y2022

import net.fish.Day
import net.fish.maths.LongCircArrayEntry
import net.fish.maths.MovingEntriesCircularArray
import net.fish.resourceLines

object Day20 : Day {
    private val data by lazy { resourceLines(2022, 20).map { it.toLong() } }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data.map { it * 811589153 })

    fun doPart1(data: List<Long>): Long = decrypt(data)
    fun doPart2(data: List<Long>): Long = decrypt(data, 10)

    private fun decrypt(data: List<Long>, iterations: Int = 1): Long {
        val circ = MovingEntriesCircularArray(data)
        val entries = data.mapIndexed { i, d -> LongCircArrayEntry(d, i) }
        (1..iterations).forEach { _ ->
            entries.forEach { e ->
                circ.move(e, e.v)
            }
        }
        val indexOfZero = circ.findZero()
        val i1 = circ.at(indexOfZero + 1000)
        val i2 = circ.at(indexOfZero + 2000)
        val i3 = circ.at(indexOfZero + 3000)
        return i1 + i2 + i3
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}