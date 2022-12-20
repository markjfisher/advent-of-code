package net.fish.y2022

import net.fish.Day
import net.fish.maths.LongCircArrayEntry
import net.fish.maths.MovingEntriesCircularArray
import net.fish.resourceLines

object Day20 : Day {
    private val data by lazy { resourceLines(2022, 20).map { it.toLong() } }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data.map { it * 811589153 })

    fun doPart1(data: List<Long>): Long = coordinates(decrypt(data))
    fun doPart2(data: List<Long>): Long = coordinates(decrypt(data, 10))

    fun decrypt(data: List<Long>, iterations: Int = 1): MovingEntriesCircularArray {
        val circ = MovingEntriesCircularArray(data)
        val entries = data.mapIndexed { i, d -> LongCircArrayEntry(d, i) }
        (1..iterations).forEach { _ ->
            entries.forEach { e ->
                circ.move(e, e.v)
            }
        }
        return circ
    }

    private fun coordinates(circ: MovingEntriesCircularArray): Long {
        val indexOfZero = circ.findZero()
        return listOf(1000, 2000, 3000).sumOf { circ.at(indexOfZero + it) }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}