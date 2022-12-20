package net.fish.y2022

import com.google.ortools.sat.CpModel
import com.google.ortools.sat.CpSolver
import com.google.ortools.sat.IntVar
import net.fish.Day
import net.fish.resourceLines

object Day19CPM : Day {

    override fun part1() = doPart1(Day19.toBlueprints(resourceLines(2022, 19)))
    override fun part2() = doPart2(Day19.toBlueprints(resourceLines(2022, 19)))

    fun doPart1(data: List<Day19.Blueprint>): Int {
        val solver = CpSolver()
        val model = CpModel()
        val x = model.newIntVar(0, 100, "x")

        return 0
    }

    fun doPart2(data: List<Day19.Blueprint>): Int = data.size

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1())
        println(part2())
    }

}