package net.fish.y2021

import net.fish.Day
import net.fish.resourceString
import kotlin.math.abs

object Day07 : Day {
    private val data by lazy { resourceString(2021, 7).split(",").map { it.toInt() } }

    override fun part1() = doPart1(data)
    override fun part2() = doPart2(data)

    fun doPart1(data: List<Int>): Pair<Int, Int> {
        return calculateMinimum(data, ::fuelCost)
    }

    fun doPart2(data: List<Int>): Pair<Int, Int> {
        return calculateMinimum(data, ::fuelCost2)
    }

    private fun calculateMinimum(data: List<Int>, minFn: (Int, List<Int>) -> Int): Pair<Int, Int> {
        return (data.indices).fold(Pair(0, 0)) { solution, i ->
            val fuelCost = minFn(i, data)
            when {
                solution.first == 0 || fuelCost < solution.first -> Pair(fuelCost, i)
                fuelCost > solution.first -> solution
                else -> throw Exception("Duplicate cost at $i, cost: $fuelCost, old solution: $solution")
            }
        }
    }

    private fun fuelCost(pos: Int, positions: List<Int>): Int = positions.sumOf { abs(it - pos) }

    private fun fuelCost2(pos: Int, positions: List<Int>): Int = positions.sumOf {
        val diff = abs(it - pos)
        diff * (diff + 1) / 2
    }


    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}